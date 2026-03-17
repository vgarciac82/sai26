const SYSTEM_URL = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1];

const loadCatalog = function (
    queryName,
    targetObjectId,
    callbackSuccess,
    callbackError
) {
    return $.ajax({
        type: "POST",
        url: SYSTEM_URL + "/crud?rt=s&ql=" + queryName,
        cache: false,
        async: true,
        data: $("form").serialize(),
        error: function (xhr, textStatus, errorThrown) {
            console.error(
                `Advertencia: ${xhr.responseText}\nEstatus: ${textStatus}\n${errorThrown}`
            );
            if (callbackError) callbackError(responseText, textStatus, errorThrown);
        },
        success: function (RS) {
            let index;
            let data;
            let col;
            let names;

            if (RS.success == "true") {
                index = 1;
                while (true) {
                    data = eval("RS.data_" + index++);

                    if (!data) break;

                    names = [];

                    for (let f in data[0]) names.push(f);

                    $("#" + targetObjectId + " option").remove();

                    for (let i = 0; i < data.length; i++) {
                        col = data[i];
                        $("#" + targetObjectId).append(
                            '<option value="' +
                            makePipeList(col, names) +
                            '">' +
                            eval("col." + names[0]) +
                            "</option>"
                        );
                    }
                }
                if (callbackSuccess) callbackSuccess();
            } else {
                if (callbackError) callbackError(RS.message, null, null);
            }
        },
    });
};

const makePipeList = function (cols, names) {
    let value, token;
    value = token = "";

    for (let i = 1; i < names.length; i++) {
        let val = eval("cols." + names[i]);

        value += token + val;
        token = "|";
    }
    return value;
};

const getSelected = function (idElement) {
    var selectElement = document.getElementById(idElement);
    var selectedValue = selectElement.options[selectElement.selectedIndex].value;
    return selectedValue;
}

const showErrorMessage = function (errorMessage) {
    Swal.fire({
        icon: 'error',
        title: 'Oops...',
        text: errorMessage,
    });
}

const formatDate = function (date) {
    var day = date.getDate();
    var month = date.getMonth() + 1;
    var year = date.getFullYear();

    if (day < 10) {
        day = "0" + day;
    }
    if (month < 10) {
        month = "0" + month;
    }

    return day + "/" + month + "/" + year;
};


const showDocument = function (selectedNode) {
    window.open(
        "../SAIFilestore?select=" + selectedNode,
        "",
        "scrollbars=1, resizable=yes, width=900, height=800"
    );
};

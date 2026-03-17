const ctx = document.getElementById('consumptionChart');
let chart = null;

const dataAsignations = {
    labels: [
        'Disponible',
        'Dispersado'
    ],
    datasets: [{

        data: [0, 0],
        backgroundColor: [

            'rgb(255, 205, 86)',
            'rgb(54, 162, 235)'
        ],
        hoverOffset: 4
    }]
};


const config = {
    type: 'pie',
    data: dataAsignations,
    options: {
        aspectRatio: 1,
    },
    scales: {
        x: {
            max: 150
        },
        y: {
            max: 150
        }
    }
};

const initAccountAsignationReport = function (idExecutiveUnit) {
    commonContractInit(idExecutiveUnit);
    $("#loadAccountInfoBtn").click(function () {
        loadAccountInfo();
        loadAssignationInfo();
    });
}


$(document).ready(function () {
    initAccountAsignationReport(executiveUnit);
    
    if( $("#asignationMonth").val() == "" )
    	$("#asignationMonth").val(new Date().getMonth() + 1);
    
    $("#asignationMonth").change(function () {
        loadSummary(onSuccesLoadSummary, onErrorLoadSummary);
    });

    $("#generateReport").click(function () {
        downloadReport();
    });
});

const downloadReport = function () {
    let accountId = $("#idAccount").val();
    let month = $("#asignationMonth").val();
    let url = `ExportMonthlyAssinationSummary?account=${accountId}&month=${month}`;
    window.open(url, '_blank');
}



const loadAssignationInfo = function () {
    $("#accountCardDiv").show();
    $("#actionsDiv").show();
    loadSummary(onSuccesLoadSummary, onErrorLoadSummary);
}

const loadSummary = function (onSuccess, onError) {
    let accountId = $("#idAccount").val();
    let month = $("#asignationMonth").val();

    let url = `MonthlyAssinationSummary?account=${accountId}&month=${month}`;
    $.ajax({
        url: url,
        type: "GET",
        dataType: "json",
        success: onSuccess,
        error: onError,
        beforeSend: function () { $.blockUI({ message: "Cargando Información" }); },
        complete: function () { $.unblockUI(); }
            
    });
}

const onSuccesLoadSummary = function (response) {
    console.log(response);
    updateSumaryInfo(response?.summary || null);
    updateDetailInfo(response?.detail || null);
    displayGraph(response?.summary || null);
}

const onErrorLoadSummary = function (response) {
    console.log(response);
    Swal.fire({
        icon: 'error',
        title: 'Error al cargar la información',
        text: 'Ocurrio un error al cargar la información, intente de nuevo',
        footer: ''
    });

}

const formatDate = function(milliseconds) {
    const date = new Date(milliseconds);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  }

  
const updateSumaryInfo = function (response) {
    let tr = "<tr>";
    if (response == null)
        tr += `<td colspan="4">No hay información disponible</td>`;
    else {

        tr += `<td>${response.asignationAutorizedAmount}</td>`;
        tr += `<td>${response.totalFuelingAuthorizedAmount}</td>`;
        tr += `<td>${response.fuelingRefundAmount}</td>`;
        tr += `<td>${response.asignationAutorizedAmount - response.fuelingAuthorizedAmount}</td>`;
    }

    tr += `</tr>`;
    $("#asignedSummary tbody").html(tr);

}

const updateDetailInfo = function (response) {
    let tr = "";
    if (response == null || response.length == 0)
        tr += `<tr><td colspan="3">No hay información disponible</td></tr>`;
    else {
        response.forEach(function (item) {
            tr += `<tr>`;
            tr += `<td>${formatDate(item.requestDate)}</td>`;
            tr += `<td>${item.requestedAmount}</td>`;
            tr += `<td>${item.authorizedAmount}</td>`;
            tr += `</tr>`;
        });
    }
    $("#detailTable tbody").html(tr);
}

const displayGraph = function (response) {

    let available = 0;
    let totalFuelingAuthorizedAmount = 0;

    if (response != null) {
        available = response.asignationAutorizedAmount - response.fuelingAuthorizedAmount;
        if (available < 0) available = 0;
        totalFuelingAuthorizedAmount = response.totalFuelingAuthorizedAmount
    }

    dataAsignations.datasets[0].data = [available, response.fuelingAuthorizedAmount];

    if (chart != null) {
        chart.data = dataAsignations; // Update the chart data
        chart.update(); // Update the chart
    } else {
        chart = new Chart(ctx, config); // Create a new chart
    }
}
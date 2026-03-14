package com.syc.viewer;

import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.OutputStream;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import com.sun.media.jai.codec.FileSeekableStream;
import java.util.Base64;

public class ImageManagerJAI {

    public static RenderedOp load(String file) throws IOException {
        FileSeekableStream fss = new FileSeekableStream(file);
        return JAI.create("stream", fss);
    }

    public static void writeResult(OutputStream os, RenderedOp image, String type) throws IOException {
        if ("GIF".equals(type)) {
            GifEncoder encoder = new GifEncoder(image.getAsBufferedImage(), os);
            encoder.encode();
        } else {
            JAI.create("encode", image, os, type, null);
        }
    }

    public static RenderedOp setSize(RenderedOp image, float scale) {
        ParameterBlock params = new ParameterBlock();
        params.addSource(image);
        params.add(scale / 100f);
        params.add(scale / 100f);
        params.add(0.0F);
        params.add(0.0F);
        params.add(new InterpolationBilinear());
        return JAI.create("scale", params);
    }

    public static RenderedOp thumbnail(RenderedOp image, float edgeLength) {
        boolean tall = (image.getHeight() > image.getWidth());
        float modifier = edgeLength / (float) (tall ? image.getHeight() : image.getWidth());
        ParameterBlock params = new ParameterBlock();
        params.addSource(image);
        // x scale factor
        params.add(modifier);
        // y scale factor
        params.add(modifier);
        // x translate
        params.add(0.0F);
        // y translate
        params.add(0.0F);
        // interpolation method
        params.add(new InterpolationBilinear());
        return JAI.create("scale", params);
    }

    public static RenderedOp rotate(RenderedOp image, float degree) {
        float centerX = image.getWidth() / 2f;
        float centerY = image.getHeight() / 2f;
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(centerX);
        pb.add(centerY);
        pb.add((float) Math.toRadians(degree));
        pb.add(new InterpolationBilinear());
        return JAI.create("rotate", pb);
    }

    public static RenderedOp crop(RenderedOp image, float edge) {
        ParameterBlock params = new ParameterBlock();
        params.addSource(image);
        // x origin
        params.add(edge);
        // y origin
        params.add(edge);
        // width
        params.add((float) image.getWidth() - edge);
        // height
        params.add((float) image.getHeight() - edge);
        return JAI.create("crop", params);
    }

    public static RenderedOp border(RenderedOp image, int edge, double edgeColor) {
        ParameterBlock params = new ParameterBlock();
        params.addSource(image);
        // left pad
        params.add(edge);
        // right pad
        params.add(edge);
        // top pad
        params.add(edge);
        // bottom pad
        params.add(edge);
        double[] fill = { edgeColor };
        // type
        params.add(new BorderExtenderConstant(fill));
        // fill color
        params.add(edgeColor);
        return JAI.create("border", params);
    }
}

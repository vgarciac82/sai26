package com.syc.viewer;

import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.OutputStream;

import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.FileSeekableStream;

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

		params.add(modifier); // x scale factor
		params.add(modifier); // y scale factor
		params.add(0.0F); // x translate
		params.add(0.0F); // y translate
		params.add(new InterpolationBilinear()); // interpolation method

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

		params.add(edge); // x origin
		params.add(edge); // y origin
		params.add((float) image.getWidth() - edge); // width
		params.add((float) image.getHeight() - edge); // height

		return JAI.create("crop", params);
	}

	public static RenderedOp border(RenderedOp image, int edge, double edgeColor) {
		ParameterBlock params = new ParameterBlock();
		params.addSource(image);

		params.add(edge); // left pad
		params.add(edge); // right pad
		params.add(edge); // top pad
		params.add(edge); // bottom pad
		double fill[] = { edgeColor };
		params.add(new BorderExtenderConstant(fill)); // type
		params.add(edgeColor); // fill color

		return JAI.create("border", params);
	}
}

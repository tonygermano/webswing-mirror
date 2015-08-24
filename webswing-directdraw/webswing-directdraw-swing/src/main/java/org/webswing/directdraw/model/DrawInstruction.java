package org.webswing.directdraw.model;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.*;
import org.webswing.directdraw.toolkit.*;

public class DrawInstruction {

	private InstructionProto instruction;
	private DrawConstant[] args;
	private WebImage image;

	public DrawInstruction(InstructionProto type, DrawConstant... args) {
		instruction = type;
		this.args = args;
	}

	public DrawInstruction(WebImage image, DrawConstant... args) {
		instruction = InstructionProto.DRAW_WEBIMAGE;
		this.image = image;
		this.args = args;
	}

	public DrawConstant[] getArgs() {
		return args;
	}

	public void setArgs(DrawConstant[] args) {
		this.args = args;
	}

	public WebImage getImage() {
		return image;
	}

	public InstructionProto getInstruction() {
		return instruction;
	}

	public DrawInstructionProto toMessage(DirectDraw dd) {
		DrawInstructionProto.Builder builder = DrawInstructionProto.newBuilder();
		builder.setInst(instruction);
		for (DrawConstant c : args) {
			builder.addArgs(c.getId());
		}
		if (image != null) {
			builder.setWebImage(image.toMessage(dd).toByteString());
		}
		return builder.build();
	}

	@Override
	public String toString() {
		return instruction.name();
	}
}

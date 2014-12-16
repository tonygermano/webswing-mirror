package org.webswing.directdraw.model;

import java.util.ArrayList;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.InstructionProto;
import org.webswing.directdraw.toolkit.WebImage;

public class DrawInstruction {

	private InstructionProto instruction;
	private DrawConstant[] args;
	private WebImage image;

	public DrawInstruction(InstructionProto type, DrawConstant... args) {
		instruction = type;
		ArrayList<DrawConstant> constants = new ArrayList<DrawConstant>();
		for (DrawConstant c : args) {
			if (c != null) {
				constants.add(c);
			}
		}
		this.args = constants.toArray(new DrawConstant[constants.size()]);
	}

	public DrawInstruction(WebImage image, DrawConstant... args) {
		instruction = InstructionProto.DRAW_WEBIMAGE;
		this.image = image;
		ArrayList<DrawConstant> constants = new ArrayList<DrawConstant>();
		for (DrawConstant c : args) {
			if (c != null) {
				constants.add(c);
			}
		}
		this.args = constants.toArray(new DrawConstant[constants.size()]);
	}

	public DrawConstant[] getArgs() {
		return args;
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
			builder.addArgs(c.getAddress());
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

package com.jerotes.jerotes.spell;

public class MagicType {
	public static final MagicType TARGET = new TargetMagic();
	public static final MagicType SHOOT = new ShootMagic();
	public static final MagicType SELF = new SelfMagic();

	public static final MagicType MAIN = new MainMagic();
	public static final MagicType ADD = new AddMagic();
	public static final MagicType SPECIAL = new SpeicalMagic();

	public MagicType() {

	}

	public static MagicType[] types() {
		return new MagicType[]{TARGET, SHOOT, SELF};
	}

	private static class TargetMagic extends MagicType {
	}
	private static class ShootMagic extends MagicType {
	}
	private static class SelfMagic extends MagicType {
	}
	private static class MainMagic extends MagicType {
	}
	private static class AddMagic extends MagicType {
	}
	private static class SpeicalMagic extends MagicType {
	}
}
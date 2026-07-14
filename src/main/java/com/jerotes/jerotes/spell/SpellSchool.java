package com.jerotes.jerotes.spell;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SpellSchool {
	public static final SpellSchool ABJURATION = new AbjurationMagic(); //防护系
	public static final SpellSchool CONJURATION = new ConjurationMagic(); //咒法系
	public static final SpellSchool DIVINATION = new DivinationMagic(); //预言系
	public static final SpellSchool ENCHANTMENT = new EnchantmentMagic(); //附魔系
	public static final SpellSchool EVOCATION = new EvocationMagic(); //塑能系
	public static final SpellSchool ILLUSION = new IllusionMagic(); //幻术系
	public static final SpellSchool NECROMANCY = new NecromancyMagic(); //死灵系
	public static final SpellSchool TRANSMUTATION = new TransmutationMagic(); //变化系
	public static final SpellSchool PSIONICS = new PsionicsMagic(); //灵能
	public static final SpellSchool SKILL = new SkillMagic(); //技巧

	public SpellSchool() {
	}

	public static SpellSchool[] types() {
		return new SpellSchool[]{
				ABJURATION, CONJURATION, DIVINATION, ENCHANTMENT,
				EVOCATION, ILLUSION, NECROMANCY, TRANSMUTATION,
				PSIONICS, SKILL
		};
	}

	public String getTranslationKey() {
		return "spell.jerotes.school.evocation";
	}
	public Component getDisplayName() {
		return Component.translatable(getTranslationKey()).withStyle(getColor());
	}

	public ChatFormatting getColor() {
		return ChatFormatting.WHITE;
	}

	private static class AbjurationMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.abjuration";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.DARK_AQUA;
		}
	}
	private static class ConjurationMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.conjuration";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.YELLOW;
		}
	}
	private static class DivinationMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.divination";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.AQUA;
		}
	}
	private static class EnchantmentMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.enchantment";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.DARK_PURPLE;
		}
	}
	private static class EvocationMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.evocation";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.RED;
		}
	}
	private static class IllusionMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.illusion";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.BLUE;
		}
	}
	private static class NecromancyMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.necromancy";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.GREEN;
		}
	}
	private static class TransmutationMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.transmutation";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.GOLD;
		}
	}
	private static class PsionicsMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.psionics";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.LIGHT_PURPLE;
		}
	}
	private static class SkillMagic extends SpellSchool {
		@Override
		public String getTranslationKey() {
			return "spell.jerotes.school.skill";
		}
		@Override
		public ChatFormatting getColor() {
			return ChatFormatting.GRAY;
		}
	}
}
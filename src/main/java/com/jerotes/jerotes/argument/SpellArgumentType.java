package com.jerotes.jerotes.argument;

import com.jerotes.jerotes.spell.SpellRegistry;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;

public class SpellArgumentType {
	public static final SuggestionProvider<CommandSourceStack> SPELL_ID_SUGGESTIONS = (context, builder) -> {
		for (String spellId : SpellRegistry.getRegisteredSpellIds()) {
			builder.suggest(spellId);
		}
		return builder.buildFuture();
	};
}

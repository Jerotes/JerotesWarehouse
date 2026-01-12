package com.jerotes.jerotes.spell;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {
	private static final Map<String, SpellTypeInterface> REGISTRY = new HashMap<>();

	static {
		for (SpellType type : SpellType.values()) {
			register(type);
		}
	}

	public static void register(SpellTypeInterface spellType) {
		if (REGISTRY.containsKey(spellType.getId())) {
			throw new IllegalArgumentException("重复的法术ID: " + spellType.getId());
		}
		REGISTRY.put(spellType.getId(), spellType);
	}

	public static SpellTypeInterface getSpellTypeById(String id) {
		SpellTypeInterface type = REGISTRY.get(id);
		if (type == null) {
			throw new IllegalArgumentException("未知法术类型: " + id);
		}
		return type;
	}

	public static boolean spellExists(String id) {
		return REGISTRY.containsKey(id);
	}

	public static Collection<String> getRegisteredSpellIds() {
		return REGISTRY.keySet();
	}
}
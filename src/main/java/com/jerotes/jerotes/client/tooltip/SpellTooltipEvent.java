package com.jerotes.jerotes.client.tooltip;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.item.FlawlessMagicScroll;
import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.spell.MagicSpell;
import com.jerotes.jerotes.spell.SpellListByString;
import com.jerotes.jerotes.spell.SpellRegistry;
import com.mojang.datafixers.util.Either;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, value = Dist.CLIENT)
public class SpellTooltipEvent {

    @SubscribeEvent
    public static void gather(RenderTooltipEvent.GatherComponents event) {
        if (!(event.getItemStack().getItem() instanceof MagicItem scroll && scroll.showTooltipIcon()))
            return;

        String spellId = scroll.getSpellIdMagicItem(event.getItemStack());

        if (spellId.isEmpty())
            return;

        if (!SpellRegistry.spellExists(spellId))
            return;

        MagicSpell spell = SpellListByString.getSpell(
                1,
                null,
                null,
                SpellRegistry.getSpellTypeById(spellId)
        );

        if (spell == null)
            return;

        if (spell.getDisplayResourceLocation() == null)
            return;

        event.getTooltipElements().add(
                0,
                Either.right(
                        new SpellTooltipComponent(
                                spell.getDisplayResourceLocation()
                        )
                )
        );
    }
}
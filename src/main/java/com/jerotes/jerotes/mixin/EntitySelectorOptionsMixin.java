package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.FactionEntity;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;

@Mixin(EntitySelectorOptions.class)
public class EntitySelectorOptionsMixin {
    @Inject(method = "bootStrap", at = @At("RETURN"))
    private static void registerCustomOptions(CallbackInfo ci) {
        EntitySelectorOptions.register(
                "jerotes_faction",
                (parser) -> {
                    StringReader reader = parser.getReader();
                    String faction = reader.readString();
                    parser.addPredicate((entity) -> {
                        List<String> finalStringList = FactionEntity.getFactionTypeListAll(entity);
                        return finalStringList.contains(faction);
                    });
                },
                (parser) -> true,
                Component.translatable("argument.entity.options.jerotes_faction.description")
        );
    }
}
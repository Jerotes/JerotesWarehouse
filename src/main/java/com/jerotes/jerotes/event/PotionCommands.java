package com.jerotes.jerotes.event;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class PotionCommands {

    private PotionCommands() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build(RegisterCommandsEvent event) {
        return Commands.literal("potion")
                .then(add())
                .then(custom(event))
                .then(remove())
                .then(get());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> add() {
        return Commands.literal("add")
                .then(Commands.argument("effect", ResourceLocationArgument.id())
                        .suggests((context, builder) -> {
                            for (ResourceLocation id : ForgeRegistries.MOB_EFFECTS.getKeys()) {
                                builder.suggest(id.toString());
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> addEffect(context, 600, 0))
                        .then(Commands.argument("ticks", IntegerArgumentType.integer(1, 72000))
                                .executes(context -> addEffect(context, IntegerArgumentType.getInteger(context, "ticks"), 0))
                                .then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255))
                                        .executes(context -> addEffect(context,
                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                IntegerArgumentType.getInteger(context, "amplifier"))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> custom(RegisterCommandsEvent event) {
        return Commands.literal("custom")
                .then(Commands.argument("item", ItemArgument.item(event.getBuildContext()))
                        .then(Commands.argument("effect", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    for (ResourceLocation id : ForgeRegistries.MOB_EFFECTS.getKeys()) {
                                        builder.suggest(id.toString());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> customPotion(context, 600, 0))
                                .then(Commands.argument("ticks", IntegerArgumentType.integer(1, 72000))
                                        .executes(context -> customPotion(context, IntegerArgumentType.getInteger(context, "ticks"), 0))
                                        .then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255))
                                                .executes(context -> customPotion(context,
                                                        IntegerArgumentType.getInteger(context, "ticks"),
                                                        IntegerArgumentType.getInteger(context, "amplifier")))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("get")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests((context, builder) -> {
                            for (ResourceLocation id : ForgeRegistries.POTIONS.getKeys()) {
                                builder.suggest(id.toString());
                            }
                            for (ResourceLocation id : ForgeRegistries.MOB_EFFECTS.getKeys()) {
                                builder.suggest(id.toString());
                            }
                            return builder.buildFuture();
                        })
                        .executes(PotionCommands::givePotion));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> remove() {
        return Commands.literal("remove")
                .then(Commands.literal("all").executes(PotionCommands::clearEffects))
                .then(Commands.argument("effect", ResourceLocationArgument.id())
                        .suggests((context, builder) -> {
                            for (ResourceLocation id : ForgeRegistries.MOB_EFFECTS.getKeys()) {
                                builder.suggest(id.toString());
                            }
                            return builder.buildFuture();
                        })
                        .executes(PotionCommands::removeEffect));
    }

    private static int removeEffect(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation key = ResourceLocationArgument.getId(context, "effect");
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            throw new SimpleCommandExceptionType(Component.literal("主手没有物品")).create();
        }
        List<MobEffectInstance> effects = new ArrayList<>(PotionUtils.getCustomEffects(stack));
        int before = effects.size();
        effects.removeIf(instance -> instance.getEffect() == ForgeRegistries.MOB_EFFECTS.getValue(key));
        if (effects.size() == before) {
            throw new SimpleCommandExceptionType(Component.literal("没有这个效果: " + key)).create();
        }
        writeEffects(stack, effects);
        context.getSource().sendSuccess(() -> Component.literal("[remove] 移除 " + key + "  剩余 " + effects.size() + " 条"), false);
        return 1;
    }

    private static int clearEffects(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            throw new SimpleCommandExceptionType(Component.literal("主手没有物品")).create();
        }
        writeEffects(stack, new ArrayList<>());
        context.getSource().sendSuccess(() -> Component.literal("[remove all] 已清空药水效果"), false);
        return 1;
    }

    private static void writeEffects(ItemStack stack, List<MobEffectInstance> effects) {
        if (effects.isEmpty()) {
            stack.removeTagKey("CustomPotionEffects");
            if (stack.getTag() != null) {
                stack.getTag().remove("CustomPotionColor");
            }
            return;
        }
        PotionUtils.setCustomEffects(stack, effects);
        stack.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(effects));
    }

    private static MobEffect effectOf(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ResourceLocation key = ResourceLocationArgument.getId(context, name);
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(key);
        if (effect == null) {
            throw new SimpleCommandExceptionType(Component.literal("未知效果: " + key)).create();
        }
        return effect;
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static int addEffect(CommandContext<CommandSourceStack> context, int ticks, int amplifier) throws CommandSyntaxException {
        MobEffect effect = effectOf(context, "effect");
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            throw new SimpleCommandExceptionType(Component.literal("主手没有物品")).create();
        }
        List<MobEffectInstance> effects = new ArrayList<>(PotionUtils.getCustomEffects(stack));
        effects.add(new MobEffectInstance(effect, ticks, amplifier));
        PotionUtils.setCustomEffects(stack, effects);
        stack.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(effects));
        context.getSource().sendSuccess(() -> Component.literal("[add] " + effect.getDisplayName().getString()
                + "  刻=" + ticks + "  强度=" + (amplifier + 1)), false);
        return 1;
    }

    private static int customPotion(CommandContext<CommandSourceStack> context, int ticks, int amplifier) throws CommandSyntaxException {
        MobEffect effect = effectOf(context, "effect");
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = ItemArgument.getItem(context, "item").createItemStack(1, false);
        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(effect, ticks, amplifier));
        PotionUtils.setCustomEffects(stack, effects);
        stack.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(effects));
        give(player, stack);
        context.getSource().sendSuccess(() -> Component.literal("[custom] " + effect.getDisplayName().getString()
                + "  刻=" + ticks + "  强度=" + (amplifier + 1)), false);
        return 1;
    }

    private static int givePotion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation key = ResourceLocationArgument.getId(context, "id");
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = new ItemStack(Items.POTION);

        Potion potion = ForgeRegistries.POTIONS.getValue(key);
        if (potion != null) {
            PotionUtils.setPotion(stack, potion);
        }
        else {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(key);
            if (effect == null) {
                throw new SimpleCommandExceptionType(Component.literal("未知药水或效果: " + key)).create();
            }
            List<MobEffectInstance> effects = new ArrayList<>();
            effects.add(new MobEffectInstance(effect, 600, 0));
            PotionUtils.setCustomEffects(stack, effects);
            stack.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(effects));
        }
        give(player, stack);
        return 1;
    }
}

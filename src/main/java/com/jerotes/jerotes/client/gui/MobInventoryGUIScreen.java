package com.jerotes.jerotes.client.gui;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.BossEntity;
import com.jerotes.jerotes.entity.EliteEntity;
import com.jerotes.jerotes.entity.InventoryEntity;
import com.jerotes.jerotes.entity.JerotesEntity;
import com.jerotes.jerotes.network.*;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import com.jerotes.jerotes.world.inventory.MobInventoryGUIMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MobInventoryGUIScreen extends AbstractContainerScreen<MobInventoryGUIMenu> {
    private final Player player;
    public static final ResourceLocation YES = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/mob_inventory/yes.png");
    public static final ResourceLocation SLOT = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/mob_inventory/slot.png");
    public static final ResourceLocation NO = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/mob_inventory/no.png");
    public static final ResourceLocation EFFECT = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/mob_inventory/effect.png");
    public static final ResourceLocation SHARE = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/mob_inventory/share.png");
    public static final ResourceLocation LOCK = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/mob_inventory/lock.png");

    public MobInventoryGUIScreen(MobInventoryGUIMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.player = container.player;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private static final ResourceLocation TEXTURE = new ResourceLocation(JerotesWarehouse.MODID,"textures/gui/container/mob_inventory_gui.png");
    private static final ResourceLocation TEXTURE_INVENTORY = new ResourceLocation(JerotesWarehouse.MODID,"textures/gui/container/mob_inventory_gui_inventory.png");

    //1.20.1
    public static void renderEntityInInventoryFollowsMouse(GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_, float p_275604_, float p_275546_, LivingEntity p_275689_) {
        float f = (float)Math.atan((double)(p_275604_ / 40.0F));
        float f1 = (float)Math.atan((double)(p_275546_ / 40.0F));
        InventoryScreen.renderEntityInInventoryFollowsAngle(p_282802_, p_275688_, p_275245_, p_275535_, f, f1, p_275689_);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.menu.boundEntity instanceof LivingEntity livingEntity) {
            //本身
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, (this.width - this.imageWidth) / 2 + 33, (this.height - this.imageHeight) / 2 + 54, 17, (float)((this.width - this.imageWidth) / 2 + 33) - mouseX, (float)((this.height - this.imageHeight) / 2 + 75 - 50 - 9) - mouseY, livingEntity);

            if (!this.menu.isCanUseMainHand() ||
                    (Main.isCanNotMove(livingEntity.getItemBySlot(EquipmentSlot.MAINHAND)))) {
                guiGraphics.blit(LOCK, this.leftPos + 8, this.topPos + 62, 0, 0, 16, 16, 16, 16);
            }
            if (!this.menu.isCanUseOffHand() ||
                    (Main.isCanNotMove(livingEntity.getItemBySlot(EquipmentSlot.OFFHAND)))) {
                guiGraphics.blit(LOCK, this.leftPos + 26, this.topPos + 62, 0, 0, 16, 16, 16, 16);
            }
            if (!this.menu.isCanUseHelmet() ||
                    (Main.isCanNotMove(livingEntity.getItemBySlot(EquipmentSlot.HEAD)))) {
                guiGraphics.blit(LOCK, this.leftPos + 62, this.topPos + 8, 0, 0, 16, 16, 16, 16);
            }
            if (!this.menu.isCanUseChestplate() ||
                    (Main.isCanNotMove(livingEntity.getItemBySlot(EquipmentSlot.CHEST)))) {
                guiGraphics.blit(LOCK, this.leftPos + 62, this.topPos + 26, 0, 0, 16, 16, 16, 16);
            }
            if (!this.menu.isCanUseLeggings() ||
                    (Main.isCanNotMove(livingEntity.getItemBySlot(EquipmentSlot.LEGS)))) {
                guiGraphics.blit(LOCK, this.leftPos + 62, this.topPos + 44, 0, 0, 16, 16, 16, 16);
            }
            if (!this.menu.isCanUseBoots() ||
                    (Main.isCanNotMove(livingEntity.getItemBySlot(EquipmentSlot.FEET)))) {
                guiGraphics.blit(LOCK, this.leftPos + 62, this.topPos + 62, 0, 0, 16, 16, 16, 16);
            }

            //名称
            Component name = livingEntity.getName();

            if (livingEntity instanceof OwnableEntity ownable) {
                LivingEntity livingOwner = null;
                if (ownable.getOwner() != null) {
                    livingOwner = ownable.getOwner();
                }
                else {
                    List<Mob> list = livingEntity.level().getEntitiesOfClass(Mob.class, livingEntity.getBoundingBox().inflate(64.0, 64.0, 64.0));
                    for (Mob owner : list) {
                        if (owner == null) continue;
                        if (owner.getUUID() != ownable.getOwnerUUID()) continue;
                        livingOwner = owner;
                        break;
                    }
                }
                if (livingOwner != null) {
                    name = Component.literal("").append(livingEntity.getName()).append("(").append(livingOwner.getName()).append(")");
                }
            }
            int nameColor = 2500134;
            if (livingEntity instanceof Mob mob && mob.isAggressive()) {
                nameColor = 6816515;
            }
            drawCenteredString(guiGraphics, this.font, name, this.leftPos + 80, this.topPos + 8, this.leftPos + 167, this.topPos + 32, nameColor);

            //是否可更换装备
            if (livingEntity instanceof InventoryEntity inventory) {

                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                float scale = 0.75f;
                poseStack.scale(scale, scale, scale);
                Component component1 = inventory.componentInventoryIYes();
                Component component2 = inventory.componentInventoryINo();
                Component component3 = inventory.componentInventoryIIYes();
                Component component4 = inventory.componentInventoryIINo();
                if (inventory.hasCanChangeInventory()) {
                    if (inventory.isCanChangeInventory()) {
                        drawCenteredString(guiGraphics, this.font, component1, (int) ((this.leftPos + 176 + 8) / scale), (int) ((this.topPos + 8) / scale), (int) ((this.leftPos + 176 + 57) / scale), (int) ((this.topPos + 36) / scale), 2500134);
                    } else {
                        drawCenteredString(guiGraphics, this.font, component2, (int) ((this.leftPos + 176 + 8) / scale), (int) ((this.topPos + 8) / scale), (int) ((this.leftPos + 176 + 57) / scale), (int) ((this.topPos + 36) / scale), 2500134);
                    }
                }
                if (inventory.hasCanChangeMeleeOrRange()) {
                    if (inventory.isCanChangeMeleeOrRange()) {
                        drawCenteredString(guiGraphics, this.font, component3, (int) ((this.leftPos + 176 + 8) / scale), (int) ((this.topPos + 28) / scale), (int) ((this.leftPos + 176 + 57) / scale), (int) ((this.topPos + 56) / scale), 2500134);
                    } else {
                        drawCenteredString(guiGraphics, this.font, component4, (int) ((this.leftPos + 176 + 8) / scale), (int) ((this.topPos + 28) / scale), (int) ((this.leftPos + 176 + 57) / scale), (int) ((this.topPos + 56) / scale), 2500134);
                    }
                }
                poseStack.popPose();
                if (this.menu.isCanUse()) {
                    if (inventory.hasCanChangeInventory()) {
                        if (inventory.isCanChangeInventory()) {
                            guiGraphics.blit(YES, this.leftPos + 176 + 58, this.topPos + 8, 0, 0, 20, 20, 20, 20);
                        } else {
                            guiGraphics.blit(NO, this.leftPos + 176 + 58, this.topPos + 8, 0, 0, 20, 20, 20, 20);
                        }
                    }
                    if (inventory.hasCanChangeMeleeOrRange()) {
                        if (inventory.isCanChangeMeleeOrRange()) {
                            guiGraphics.blit(YES, this.leftPos + 176 + 58, this.topPos + 28, 0, 0, 20, 20, 20, 20);
                        } else {
                            guiGraphics.blit(NO, this.leftPos + 176 + 58, this.topPos + 28, 0, 0, 20, 20, 20, 20);
                        }
                    }


                    //1.20.1
                    if (inventory.hasCanChangeInventory()) {
                    this.addRenderableWidget(new ImageButton(this.leftPos + 176 + 58, this.topPos + 8, 20, 20, 0, 0, 19, new ResourceLocation(JerotesWarehouse.MODID, "container/null"), (button) -> {
                        button.setPosition(this.leftPos + 176 + 58, this.topPos + 8);
                        if (livingEntity.level().isClientSide) {
                            PacketHandler.sendToServer(new MobInventoryInventoryPacket(livingEntity.getId()));
                        }
                    }));
                    }
                    if (inventory.hasCanChangeMeleeOrRange()) {
                    this.addRenderableWidget(new ImageButton(this.leftPos + 176 + 58, this.topPos + 28, 20, 20, 0, 0, 19, new ResourceLocation(JerotesWarehouse.MODID, "container/null"), (button) -> {
                        button.setPosition(this.leftPos + 176 + 58, this.topPos + 8);
                        if (livingEntity.level().isClientSide) {
                            PacketHandler.sendToServer(new MobInventoryMeleeOrRangePacket(livingEntity.getId()));
                        }
                    }));
                    }
                }
            }

            //属性显示
            int leftPosAdd = 82;
            int topPos = 28;
            Component attribute;
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            float scale = 0.6f;
            poseStack.scale(scale, scale, scale);

            //血量
            if (livingEntity.getAttribute(Attributes.MAX_HEALTH) != null && livingEntity.getAttributeValue(Attributes.MAX_HEALTH) != 0) {
                String add = "(" + livingEntity.getAttributeBaseValue(Attributes.MAX_HEALTH) + ")";
                if (livingEntity.getAttributeBaseValue(Attributes.MAX_HEALTH) == livingEntity.getAttributeValue(Attributes.MAX_HEALTH)) {
                    add = "";
                }
                attribute = Component.translatable("attribute.name.generic.max_health")
                        .append(": " + livingEntity.getHealth() + "/" + livingEntity.getAttributeValue(Attributes.MAX_HEALTH)
                                + add);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
                topPos += (int) (font.lineHeight * scale);
            }
            //护甲
            if (livingEntity.getAttribute(Attributes.ARMOR) != null && livingEntity.getAttributeValue(Attributes.ARMOR) != 0) {
                String add = "(" + livingEntity.getAttributeBaseValue(Attributes.ARMOR) + ")";
                if (livingEntity.getAttributeBaseValue(Attributes.ARMOR) == livingEntity.getAttributeValue(Attributes.ARMOR)) {
                    add = "";
                }
                attribute = Component.translatable("attribute.name.generic.armor")
                        .append(": " + livingEntity.getAttributeValue(Attributes.ARMOR)
                                + add);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
                topPos += (int) (font.lineHeight * scale);
            }
            //盔甲韧性
            if (livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS) != null && livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS) != 0) {
                String add = "(" + livingEntity.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS) + ")";
                if (livingEntity.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS) == livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS)) {
                    add = "";
                }
                attribute = Component.translatable("attribute.name.generic.armor_toughness")
                        .append(": " + livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS)
                                + add);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
                topPos += (int) (font.lineHeight * scale);
            }
            //速度
            if (livingEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null && livingEntity.getAttributeValue(Attributes.MOVEMENT_SPEED) != 0) {
                String add = "(" + livingEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + ")";
                if (livingEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) == livingEntity.getAttributeValue(Attributes.MOVEMENT_SPEED)) {
                    add = "";
                }
                attribute = Component.translatable("attribute.name.generic.movement_speed")
                        .append(": " + livingEntity.getAttributeValue(Attributes.MOVEMENT_SPEED)
                                + add);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
                topPos += (int) (font.lineHeight * scale);
            }
            //飞行速度
            if (livingEntity.getAttribute(Attributes.FLYING_SPEED) != null && livingEntity.getAttributeValue(Attributes.FLYING_SPEED) != 0) {
                String add = "(" + livingEntity.getAttributeBaseValue(Attributes.FLYING_SPEED) + ")";
                if (livingEntity.getAttributeBaseValue(Attributes.FLYING_SPEED) == livingEntity.getAttributeValue(Attributes.FLYING_SPEED)) {
                    add = "";
                }
                attribute = Component.translatable("attribute.name.generic.flying_speed")
                        .append(": " + livingEntity.getAttributeValue(Attributes.FLYING_SPEED)
                                + add);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
                topPos += (int) (font.lineHeight * scale);
            }
            //法术等级
            double spellLevelLevel = this.menu.getSpellLevel();
            if (spellLevelLevel != 0) {
                attribute = Component.translatable("message.jerotes.spell_level")
                        .append(": " + spellLevelLevel);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
                topPos += (int) (font.lineHeight * scale);
            }
            //攻击
            double attackDamage = this.menu.getAttackDamage() / 100;
            double baseAttackDamage = this.menu.getBaseAttackDamage() / 100;
            if (livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null && attackDamage != 0) {
                String add = "(" + baseAttackDamage + ")";
                if (baseAttackDamage == attackDamage) {
                    add = "";
                }
                attribute = Component.translatable("attribute.name.generic.attack_damage")
                        .append(": " + attackDamage
                                + add);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
                topPos += (int) (font.lineHeight * scale);
            }
            //击退
            double attackKnockback = this.menu.getAttackKnockback() / 1000;
            double baseAttackKnockback = this.menu.getBaseAttackKnockback() / 1000;
            if (livingEntity.getAttribute(Attributes.ATTACK_KNOCKBACK) != null && attackKnockback != 0) {
                String add = "(" + baseAttackKnockback + ")";
                if (baseAttackKnockback == attackKnockback) {
                    add = "";
                }
                attribute = Component.translatable("attribute.name.generic.attack_knockback")
                        .append(": " + attackKnockback
                                + add);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
                topPos += (int) (font.lineHeight * scale);
            }
            double knockbackResistance = this.menu.getKnockbackResistance() / 1000;
            double baseKnockbackResistance = this.menu.getBaseKnockbackResistance() / 1000;
            //击退抗性
            if (livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null && knockbackResistance != 0) {
                String add = "(" + baseKnockbackResistance + ")";
                if (baseKnockbackResistance == knockbackResistance) {
                    add = "";
                }
                attribute = Component.translatable("attribute.name.generic.knockback_resistance")
                        .append(": " + knockbackResistance
                                + add);
                guiGraphics.drawString(font, attribute, (int) ((this.leftPos + leftPosAdd) / scale), (int) ((this.topPos + topPos) / scale), 2500134, false);
            }
            poseStack.popPose();
            //阵营
            String string = this.menu.getMobFaction();
            String string2 = this.menu.getMobFactionModId();
            ResourceLocation faction = new ResourceLocation(string2, "textures/gui/sprites/faction/" + string + ".png");
            if (!string.isEmpty()) {
                guiGraphics.blit(faction, this.leftPos + 44, this.topPos + 62, 0, 0, 16, 16, 16, 16);
                int textX = this.leftPos + 43;
                int textY = this.topPos + 61;
                int textWidth = 18;
                int textHeight = 18;
                if (mouseX >= textX && mouseX <= textX + textWidth && mouseY >= textY && mouseY <= textY + textHeight) {
                    List<Component> tooltip = new ArrayList<>();
                    //阵营
                    tooltip.add(Component.translatable("message." + string2 + "." + string).withStyle(ChatFormatting.GOLD));
                    //类型
                    tooltip.add(livingEntity.getType().getDescription());
                    //体型
                    String sizeString = "size_medium";
                    if (Main.mobSizeSmall(livingEntity)) {
                        sizeString = "size_small";
                    } else if (Main.mobSizeMedium(livingEntity)) {
                        sizeString = "size_medium";
                    } else if (Main.mobSizeLarge(livingEntity)) {
                        sizeString = "size_large";
                    } else if (Main.mobSizeGiant(livingEntity)) {
                        sizeString = "size_giant";
                    }
                    tooltip.add(Component.translatable("message.jerotes." + sizeString).withStyle(ChatFormatting.BLUE));
                    //宽
                    tooltip.add(Component.translatable("message.jerotes.width", Main.mobWidth(livingEntity)).withStyle(ChatFormatting.BLUE));
                    //高
                    tooltip.add(Component.translatable("message.jerotes.height", Main.mobHeight(livingEntity)).withStyle(ChatFormatting.BLUE));
                    //着火
                    if (menu.getFireTick() > 0) {
                        tooltip.add(Component.translatable("message.jerotes.fire", menu.getFireTick() / 20).withStyle(ChatFormatting.BLUE));
                    }
                    //冻结
                    if (menu.getFreezeTick() > 0) {
                        tooltip.add(Component.translatable("message.jerotes.freeze", menu.getFreezeTick() / 20).withStyle(ChatFormatting.BLUE));
                    }
                    if (livingEntity instanceof Mob) {
                        CompoundTag compoundTag = new CompoundTag();
                        livingEntity.addAdditionalSaveData(compoundTag);
                        //力量
                        if (compoundTag.get("Strength") != null) {
                                tooltip.add(Component.translatable("message.jerotes.mob_strength", compoundTag.get("Strength")).withStyle(ChatFormatting.AQUA));
                        }
                        //村民职业
                        if (livingEntity instanceof VillagerDataHolder villagerDataHolder) {
                            tooltip.add(Component.translatable("entity.minecraft.villager." + villagerDataHolder.getVillagerData().getProfession().name(), compoundTag.get("Strength")).withStyle(ChatFormatting.AQUA));
                        }
                        //颜色
                        if (compoundTag.contains("Color", Tag.TAG_BYTE)) {
                            String color = "color.minecraft." + DyeColor.byId(compoundTag.getByte("Color")).getName();
                            Component component = Component.translatable(color);
                            tooltip.add(Component.translatable("message.jerotes.color", component).withStyle(ChatFormatting.AQUA));
                        }
                        //成年
                        if (!(livingEntity instanceof AgeableMob ageableMob && !ageableMob.isBaby()) && (
                                compoundTag.get("IsBaby") != null && compoundTag.getBoolean("IsBaby") || livingEntity instanceof AgeableMob ageableMob && ageableMob.isBaby() || compoundTag.contains("Age", Tag.TAG_INT) && compoundTag.getInt("Age") < 0)
                        ) {
                            tooltip.add(Component.translatable("message.jerotes.mob_is_baby").withStyle(ChatFormatting.AQUA));
                        }
                        else if (livingEntity instanceof AgeableMob) {
                            tooltip.add(Component.translatable("message.jerotes.mob_not_baby").withStyle(ChatFormatting.AQUA));
                        }
                        //体型
                        if (compoundTag.get("Size") != null) {
                            tooltip.add(Component.translatable("message.jerotes.mob_size", compoundTag.get("Size")).withStyle(ChatFormatting.AQUA));
                        }
                        //信任
                        if (Main.isTrusted(livingEntity, player, false)) {
                            tooltip.add(Component.translatable("message.jerotes.trust", livingEntity.getName(), player.getName()).withStyle(ChatFormatting.GREEN));
                        }
                    }
                    //主人
                    if (livingEntity instanceof OwnableEntity ownable) {
                        LivingEntity livingOwner = null;
                        if (ownable.getOwner() != null) {
                            livingOwner = ownable.getOwner();
                        }
                        else {
                            List<Mob> list = livingEntity.level().getEntitiesOfClass(Mob.class, livingEntity.getBoundingBox().inflate(64.0, 64.0, 64.0));
                            for (Mob owner : list) {
                                if (owner == null) continue;
                                if (owner.getUUID() != ownable.getOwnerUUID()) continue;
                                livingOwner = owner;
                                break;
                            }
                        }
                        if (livingOwner != null) {
                            tooltip.add(Component.translatable("message.jerotes.owner", livingOwner.getName()).withStyle(ChatFormatting.GREEN));
                        }
                    }
                    //目标
                    if (livingEntity instanceof Mob mob && mob.isAggressive()) {
                        tooltip.add(Component.translatable("message.jerotes.aggressive").withStyle(ChatFormatting.RED));
                    }
                    //类型
                    if (livingEntity instanceof NeutralMob) {
                        tooltip.add(Component.translatable("message.jerotes.neutral").withStyle(ChatFormatting.YELLOW));
                    }
                    if (livingEntity instanceof Enemy) {
                        tooltip.add(Component.translatable("message.jerotes.enemy").withStyle(ChatFormatting.RED));
                    }
                    if (livingEntity instanceof EliteEntity) {
                        tooltip.add(Component.translatable("message.jerotes.elite").withStyle(ChatFormatting.AQUA));
                    }
                    if (EntityAndItemFind.isBoss(livingEntity.getType()) || livingEntity instanceof BossEntity) {
                        tooltip.add(Component.translatable("message.jerotes.boss").withStyle(ChatFormatting.DARK_PURPLE));
                    }
                    List<String> stringList = EntityFactionFind.getAllFindFaction(livingEntity);
                    for (String stringType : stringList) {
                        if (stringType == null) continue;
                        tooltip.add(Component.translatable("message.jerotes." + stringType).withStyle(ChatFormatting.WHITE));
                    }
                    //额外
                    if (livingEntity instanceof JerotesEntity jerotes) {
                        jerotes.MobInventoryAddTooltip(tooltip, player);
                    }
                    //阵营
                    if (livingEntity.getTeam() != null) {
                        tooltip.add(Component.translatable("message.jerotes.faction", livingEntity.getTeam().getName()).withStyle(livingEntity.getTeam().getColor()));
                    }
                    guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                }
            }

            //状态效果
            {
                int textX = this.leftPos + 8;
                int textY = this.topPos + 8;
                int textWidth = 8;
                int textHeight = 8;
                if (mouseX >= textX && mouseX <= textX + textWidth && mouseY >= textY && mouseY <= textY + textHeight) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable("message.jerotes.effect_click").withStyle(ChatFormatting.DARK_GREEN));
                    guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                }
                guiGraphics.blit(EFFECT, this.leftPos + 8, this.topPos + 8, 0, 0, 8, 8, 8, 8);
                //1.20.4

                //1.20.1
                    this.addRenderableWidget(new ImageButton(this.leftPos + 8, this.topPos + 8, 8, 8, 0, 0, 7, new ResourceLocation(JerotesWarehouse.MODID, "container/null"), (button) -> {
                        button.setPosition(this.leftPos + 8, this.topPos + 8);
                        if (livingEntity.level().isClientSide) {
                            PacketHandler.sendToServer(new MobSendEffectsPacket(livingEntity.getId()));
                        }
                    }));
            }

            //分享
            if (this.menu.isCanUse()) {
                int textX = this.leftPos + 168;
                int textY = this.topPos + 1;
                int textWidth = 6;
                int textHeight = 6;
                if (mouseX >= textX && mouseX <= textX + textWidth && mouseY >= textY && mouseY <= textY + textHeight) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable("message.jerotes.share").withStyle(ChatFormatting.YELLOW));
                    guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                }
                //1.20.4
                guiGraphics.blit(SHARE, this.leftPos + 168, this.topPos + 1, 0, 0, 6, 6, 6, 6);

                //1.20.1
                    this.addRenderableWidget(new ImageButton(this.leftPos + 168, this.topPos + 1, 6, 6, 0, 0, 7, new ResourceLocation(JerotesWarehouse.MODID, "container/null"), (button) -> {
                        button.setPosition(this.leftPos + 168, this.topPos + 1);
                        if (livingEntity.level().isClientSide) {
                            PacketHandler.sendToServer(new SendMobInventoryPacket(livingEntity.getId()));
                        }
                    }));

            }
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidgets (T t) {
        return addRenderableWidget(t);
    }

    private void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, int x1, int y1, int x2, int y2, int color) {
        int textWidth = font.width(text);
        int textHeight = font.lineHeight;
        int centerX = x1 + (x2 - x1) / 2 - textWidth / 2;
        int centerY = y1 + (y2 - y1) / 2 - textHeight / 2 - textHeight / 2;
        guiGraphics.drawString(font, text, centerX, centerY, color, false);
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        int size = 4;
        if (this.menu.boundEntity instanceof LivingEntity && this.menu.boundEntity instanceof InventoryEntity inventory) {
            guiGraphics.blit(TEXTURE_INVENTORY, this.leftPos + 176, this.topPos, 0, 0, 86, 166, 86, 166);
            Container container = inventory.mobInventory();
            //格子背景
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    guiGraphics.blit(SLOT, this.leftPos + 176 + 7 + width * 18, this.topPos + 49 + height * 18, 0, 0, 18, 18, 18, 18);
                }
            }
        }
        else if (this.menu.boundEntity instanceof AbstractVillager abstractVillager) {
            guiGraphics.blit(TEXTURE_INVENTORY, this.leftPos + 176, this.topPos, 0, 0, 86, 166, 86, 166);
            Container container = abstractVillager.getInventory();
            //格子背景
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    guiGraphics.blit(SLOT, this.leftPos + 176 + 7 + width * 18, this.topPos + 49 + height * 18, 0, 0, 18, 18, 18, 18);
                }
            }
        }
        else if (this.menu.boundEntity instanceof Piglin piglin) {
            guiGraphics.blit(TEXTURE_INVENTORY, this.leftPos + 176, this.topPos, 0, 0, 86, 166, 86, 166);
            Container container = piglin.getInventory();
            //格子背景
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    guiGraphics.blit(SLOT, this.leftPos + 176 + 7 + width * 18, this.topPos + 49 + height * 18, 0, 0, 18, 18, 18, 18);
                }
            }
        }
        else if (this.menu.boundEntity instanceof Allay allay) {
            guiGraphics.blit(TEXTURE_INVENTORY, this.leftPos + 176, this.topPos, 0, 0, 86, 166, 86, 166);
            Container container = allay.getInventory();
            //格子背景
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    guiGraphics.blit(SLOT, this.leftPos + 176 + 7 + width * 18, this.topPos + 49 + height * 18, 0, 0, 18, 18, 18, 18);
                }
            }
        }
        else if (this.menu.boundEntity instanceof Player players) {
            guiGraphics.blit(TEXTURE_INVENTORY, this.leftPos + 176, this.topPos, 0, 0, 86, 166, 86, 166);
            Container container = players.getInventory();
            //格子背景
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    guiGraphics.blit(
                            SLOT,
                            this.leftPos + 176 + 7 + width * 18,
                            this.topPos + 49 + height * 18,
                            0, 0, 18, 18, 18, 18
                    );
                }
            }
        }
        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            if (this.minecraft != null) {
                if (this.minecraft.player != null) {
                    this.minecraft.player.closeContainer();
                }
            }
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void init() {
        super.init();
    }
}

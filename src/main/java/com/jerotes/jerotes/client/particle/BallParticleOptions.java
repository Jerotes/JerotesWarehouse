package com.jerotes.jerotes.client.particle;

import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class BallParticleOptions implements ParticleOptions {

	// 默认值
	public static final float DEFAULT_START_RADIUS = 0.0F;
	public static final float DEFAULT_PEAK_RADIUS = 2.0F;
	public static final float DEFAULT_END_RADIUS = 0.0F;
	public static final int DEFAULT_LIFESPAN = 30;
	public static final int DEFAULT_START_COLOR = 0xFFFFFF; // 白色
	public static final int DEFAULT_END_COLOR = 0xFFA600;   // 橙色
	public static final float DEFAULT_ALPHA_START = 0.0F;
	public static final float DEFAULT_ALPHA_PEAK = 1.0F;
	public static final float DEFAULT_ALPHA_END = 0.0F;

	// 配置字段
	public float startRadius;
	public float peakRadius;
	public float endRadius;
	public int lifespan;

	public int startColor;   // 开始时的颜色 (ARGB)
	public int endColor;     // 结束时的颜色
	public float alphaStart; // 开始透明度
	public float alphaPeak;  // 峰值透明度（在中间某时刻）
	public float alphaEnd;   // 结束透明度

	public BallParticleOptions() {
		this(DEFAULT_START_RADIUS, DEFAULT_PEAK_RADIUS, DEFAULT_END_RADIUS,
				DEFAULT_LIFESPAN,
				DEFAULT_START_COLOR, DEFAULT_END_COLOR,
				DEFAULT_ALPHA_START, DEFAULT_ALPHA_PEAK, DEFAULT_ALPHA_END);
	}

	public BallParticleOptions(float startRadius, float peakRadius, float endRadius,
							   int lifespan,
							   int startColor, int endColor,
							   float alphaStart, float alphaPeak, float alphaEnd) {
		this.startRadius = startRadius;
		this.peakRadius = peakRadius;
		this.endRadius = endRadius;
		this.lifespan = lifespan;
		this.startColor = startColor;
		this.endColor = endColor;
		this.alphaStart = alphaStart;
		this.alphaPeak = alphaPeak;
		this.alphaEnd = alphaEnd;
	}

	public BallParticleOptions(FriendlyByteBuf buf) {
		this(buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readInt(),
				buf.readInt(), buf.readInt(),
				buf.readFloat(), buf.readFloat(), buf.readFloat());
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buf) {
		buf.writeFloat(startRadius);
		buf.writeFloat(peakRadius);
		buf.writeFloat(endRadius);
		buf.writeInt(lifespan);
		buf.writeInt(startColor);
		buf.writeInt(endColor);
		buf.writeFloat(alphaStart);
		buf.writeFloat(alphaPeak);
		buf.writeFloat(alphaEnd);
	}

	@Override
	public String writeToString() {
		return "ball_particle_config";
	}

	@Override
	public ParticleType<?> getType() {
		return JerotesParticleTypes.BALL_PARTICLE.get();
	}

	// 便捷工厂（与 renderBomb 的 beginColor/targetColor 对应）
	public static BallParticleOptions withColors(float startRadius, float peakRadius, float endRadius,
												 int lifespan,
												 int beginColor, int targetColor,
												 float alphaStart, float alphaPeak, float alphaEnd) {
		return new BallParticleOptions(startRadius, peakRadius, endRadius,
				lifespan,
				beginColor, targetColor,
				alphaStart, alphaPeak, alphaEnd);
	}

	public static final Deserializer<BallParticleOptions> DESERIALIZER = new Deserializer<>() {
		@Override
		public BallParticleOptions fromCommand(ParticleType<BallParticleOptions> type, StringReader reader) throws CommandSyntaxException {
			return new BallParticleOptions();
		}

		@Override
		public BallParticleOptions fromNetwork(ParticleType<BallParticleOptions> type, FriendlyByteBuf buf) {
			return new BallParticleOptions(buf);
		}
	};
}
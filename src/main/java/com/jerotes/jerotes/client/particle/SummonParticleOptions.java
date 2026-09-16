package com.jerotes.jerotes.client.particle;

import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class SummonParticleOptions implements ParticleOptions {

	public static final float DEFAULT_INNER_HALF_WIDTH_START = 0.35F;
	public static final float DEFAULT_INNER_HALF_WIDTH_PEAK = 0.6F;
	public static final float DEFAULT_INNER_HALF_WIDTH_END = 0.6F;
	public static final float DEFAULT_OUTER_BASE_RADIUS = 1.0F;
	public static final float DEFAULT_OUTER_PEAK_RADIUS = 2F;

	public static final float DEFAULT_INNER_HEIGHT_START = 0.0F;
	public static final float DEFAULT_INNER_HEIGHT_PEAK = 3.0F;
	public static final float DEFAULT_INNER_HEIGHT_END = 2.0F;

	public static final float DEFAULT_OUTER_HEIGHT_START = 0.5F;
	public static final float DEFAULT_OUTER_HEIGHT_PEAK = 2.5F;
	public static final float DEFAULT_OUTER_HEIGHT_END = 0.5F;

	public static final float DEFAULT_R_BOTTOM = 0.4F, DEFAULT_G_BOTTOM = 0.1F, DEFAULT_B_BOTTOM = 0.6F;
	public static final float DEFAULT_R_TOP = 1.0F, DEFAULT_G_TOP = 1.0F, DEFAULT_B_TOP = 1.0F;
	public static final float DEFAULT_ALPHA_BOTTOM = 0.9F;
	public static final float DEFAULT_ALPHA_TOP = 0.0F;

	public static final int DEFAULT_INNER_LIFESPAN = 20;
	public static final int DEFAULT_OUTER_LIFESPAN = 12;

	public float innerHalfWidthStart = DEFAULT_INNER_HALF_WIDTH_START;
	public float innerHalfWidthPeak = DEFAULT_INNER_HALF_WIDTH_PEAK;
	public float innerHalfWidthEnd = DEFAULT_INNER_HALF_WIDTH_END;
	public float outerBaseRadius = DEFAULT_OUTER_BASE_RADIUS;
	public float outerPeakRadius = DEFAULT_OUTER_PEAK_RADIUS;

	public float innerHeightStart = DEFAULT_INNER_HEIGHT_START;
	public float innerHeightPeak = DEFAULT_INNER_HEIGHT_PEAK;
	public float innerHeightEnd = DEFAULT_INNER_HEIGHT_END;

	public float outerHeightStart = DEFAULT_OUTER_HEIGHT_START;
	public float outerHeightPeak = DEFAULT_OUTER_HEIGHT_PEAK;
	public float outerHeightEnd = DEFAULT_OUTER_HEIGHT_END;

	public float rBottom = DEFAULT_R_BOTTOM, gBottom = DEFAULT_G_BOTTOM, bBottom = DEFAULT_B_BOTTOM;
	public float rTop = DEFAULT_R_TOP, gTop = DEFAULT_G_TOP, bTop = DEFAULT_B_TOP;
	public float alphaBottom = DEFAULT_ALPHA_BOTTOM;
	public float alphaTop = DEFAULT_ALPHA_TOP;

	public int innerLifespan = DEFAULT_INNER_LIFESPAN;
	public int outerLifespan = DEFAULT_OUTER_LIFESPAN;

	public SummonParticleOptions() {
		this(
				DEFAULT_INNER_HALF_WIDTH_START, DEFAULT_INNER_HALF_WIDTH_PEAK, DEFAULT_INNER_HALF_WIDTH_END,
				DEFAULT_OUTER_BASE_RADIUS, DEFAULT_OUTER_PEAK_RADIUS,
				DEFAULT_INNER_HEIGHT_START, DEFAULT_INNER_HEIGHT_PEAK, DEFAULT_INNER_HEIGHT_END,
				DEFAULT_OUTER_HEIGHT_START, DEFAULT_OUTER_HEIGHT_PEAK, DEFAULT_OUTER_HEIGHT_END,
				DEFAULT_R_BOTTOM, DEFAULT_G_BOTTOM, DEFAULT_B_BOTTOM,
				DEFAULT_R_TOP, DEFAULT_G_TOP, DEFAULT_B_TOP,
				DEFAULT_ALPHA_BOTTOM, DEFAULT_ALPHA_TOP,
				DEFAULT_INNER_LIFESPAN, DEFAULT_OUTER_LIFESPAN
		);
	}

	public SummonParticleOptions(
			float innerHalfWidthStart, float innerHalfWidthPeak, float innerHalfWidthEnd,
			float outerBaseRadius, float outerPeakRadius,
			float innerHeightStart, float innerHeightPeak, float innerHeightEnd,
			float outerHeightStart, float outerHeightPeak, float outerHeightEnd,
			float rBottom, float gBottom, float bBottom,
			float rTop, float gTop, float bTop,
			float alphaBottom, float alphaTop,
			int innerLifespan, int outerLifespan) {
		this.innerHalfWidthStart = innerHalfWidthStart;
		this.innerHalfWidthPeak = innerHalfWidthPeak;
		this.innerHalfWidthEnd = innerHalfWidthEnd;
		this.outerBaseRadius = outerBaseRadius;
		this.outerPeakRadius = outerPeakRadius;
		this.innerHeightStart = innerHeightStart;
		this.innerHeightPeak = innerHeightPeak;
		this.innerHeightEnd = innerHeightEnd;
		this.outerHeightStart = outerHeightStart;
		this.outerHeightPeak = outerHeightPeak;
		this.outerHeightEnd = outerHeightEnd;
		this.rBottom = rBottom;
		this.gBottom = gBottom;
		this.bBottom = bBottom;
		this.rTop = rTop;
		this.gTop = gTop;
		this.bTop = bTop;
		this.alphaBottom = alphaBottom;
		this.alphaTop = alphaTop;
		this.innerLifespan = innerLifespan;
		this.outerLifespan = outerLifespan;
	}

	public SummonParticleOptions(FriendlyByteBuf buf) {
		this(
				buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(),
				buf.readInt(), buf.readInt()
		);
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buf) {
		buf.writeFloat(innerHalfWidthStart).writeFloat(innerHalfWidthPeak).writeFloat(innerHalfWidthEnd);
		buf.writeFloat(outerBaseRadius).writeFloat(outerPeakRadius);
		buf.writeFloat(innerHeightStart).writeFloat(innerHeightPeak).writeFloat(innerHeightEnd);
		buf.writeFloat(outerHeightStart).writeFloat(outerHeightPeak).writeFloat(outerHeightEnd);
		buf.writeFloat(rBottom).writeFloat(gBottom).writeFloat(bBottom);
		buf.writeFloat(rTop).writeFloat(gTop).writeFloat(bTop);
		buf.writeFloat(alphaBottom).writeFloat(alphaTop);
		buf.writeInt(innerLifespan).writeInt(outerLifespan);
	}

	@Override
	public String writeToString() {
		return "summon_config";
	}

	@Override
	public ParticleType<?> getType() {
		return JerotesParticleTypes.SUMMON_PARTICLE.get();
	}

	public static SummonParticleOptions defaults() {
		return new SummonParticleOptions();
	}

	public static SummonParticleOptions withColorsAndSize(
			float targetHeight, float targetWidth,
			float rBottom, float gBottom, float bBottom,
			float rTop, float gTop, float bTop,
			float alphaBottom, float alphaTop) {
		float heightScale = targetHeight / DEFAULT_INNER_HEIGHT_PEAK;
		float widthScale  = targetWidth / DEFAULT_OUTER_PEAK_RADIUS;
		return new SummonParticleOptions(
				DEFAULT_INNER_HALF_WIDTH_START * widthScale,
				DEFAULT_INNER_HALF_WIDTH_PEAK   * widthScale,
				DEFAULT_INNER_HALF_WIDTH_END    * widthScale,
				DEFAULT_OUTER_BASE_RADIUS       * widthScale,
				DEFAULT_OUTER_PEAK_RADIUS       * widthScale,
				DEFAULT_INNER_HEIGHT_START      * heightScale,
				DEFAULT_INNER_HEIGHT_PEAK       * heightScale,
				DEFAULT_INNER_HEIGHT_END        * heightScale,
				DEFAULT_OUTER_HEIGHT_START      * heightScale,
				DEFAULT_OUTER_HEIGHT_PEAK       * heightScale,
				DEFAULT_OUTER_HEIGHT_END        * heightScale,
				rBottom, gBottom, bBottom,
				rTop, gTop, bTop,
				alphaBottom, alphaTop,
				DEFAULT_INNER_LIFESPAN, DEFAULT_OUTER_LIFESPAN
		);
	}

	public static SummonParticleOptions full(
			float innerHalfWidthStart, float innerHalfWidthPeak, float innerHalfWidthEnd,
			float outerBaseRadius, float outerPeakRadius,
			float innerHeightStart, float innerHeightPeak, float innerHeightEnd,
			float outerHeightStart, float outerHeightPeak, float outerHeightEnd,
			float rBottom, float gBottom, float bBottom,
			float rTop, float gTop, float bTop,
			float alphaBottom, float alphaTop,
			int innerLifespan, int outerLifespan) {
		return new SummonParticleOptions(
				innerHalfWidthStart, innerHalfWidthPeak, innerHalfWidthEnd,
				outerBaseRadius, outerPeakRadius,
				innerHeightStart, innerHeightPeak, innerHeightEnd,
				outerHeightStart, outerHeightPeak, outerHeightEnd,
				rBottom, gBottom, bBottom,
				rTop, gTop, bTop,
				alphaBottom, alphaTop,
				innerLifespan, outerLifespan
		);
	}

	public static final Deserializer<SummonParticleOptions> DESERIALIZER = new Deserializer<>() {
		@Override
		public SummonParticleOptions fromCommand(ParticleType<SummonParticleOptions> type, StringReader reader) throws CommandSyntaxException {
			return new SummonParticleOptions();
		}

		@Override
		public SummonParticleOptions fromNetwork(ParticleType<SummonParticleOptions> type, FriendlyByteBuf buf) {
			return new SummonParticleOptions(buf);
		}
	};
}
package com.jerotes.jerotes.client.particle;

import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class BoltParticleOptions
		implements ParticleOptions {

	public static final Codec<BoltParticleOptions> CODEC =
			RecordCodecBuilder.create(instance ->
					instance.group(
							Codec.FLOAT.fieldOf("r")
									.forGetter(o -> o.r),

							Codec.FLOAT.fieldOf("g")
									.forGetter(o -> o.g),

							Codec.FLOAT.fieldOf("b")
									.forGetter(o -> o.b),

							Codec.FLOAT.fieldOf("scale")
									.forGetter(o -> o.scale),

							Codec.FLOAT.fieldOf("dx")
									.forGetter(o -> o.dx),

							Codec.FLOAT.fieldOf("dy")
									.forGetter(o -> o.dy),

							Codec.FLOAT.fieldOf("dz")
									.forGetter(o -> o.dz)

					).apply(
							instance,
							BoltParticleOptions::new
					)
			);

	public static final ParticleOptions.Deserializer<BoltParticleOptions> DESERIALIZER =
			new ParticleOptions.Deserializer<BoltParticleOptions>() {

				@Override
				public BoltParticleOptions fromCommand(
						ParticleType<BoltParticleOptions> type,
						StringReader reader)
						throws CommandSyntaxException {

					float r = readFloat(reader);
					float g = readFloat(reader);
					float b = readFloat(reader);
					float scale = readFloat(reader);
					float dx = readFloat(reader);
					float dy = readFloat(reader);
					float dz = readFloat(reader);

					return new BoltParticleOptions(
							r,
							g,
							b,
							scale,
							dx,
							dy,
							dz
					);
				}

				private float readFloat(
						StringReader reader)
						throws CommandSyntaxException {

					reader.expect(' ');
					return reader.readFloat();
				}

				@Override
				public BoltParticleOptions fromNetwork(
						ParticleType<BoltParticleOptions> type,
						FriendlyByteBuf buf) {

					return new BoltParticleOptions(
							buf.readFloat(),
							buf.readFloat(),
							buf.readFloat(),
							buf.readFloat(),
							buf.readFloat(),
							buf.readFloat(),
							buf.readFloat()
					);
				}
			};

	private final float r;
	private final float g;
	private final float b;
	private final float scale;
	private final float dx;
	private final float dy;
	private final float dz;

	public BoltParticleOptions(
			float r,
			float g,
			float b,
			float scale,
			float dx,
			float dy,
			float dz) {

		this.r = r;
		this.g = g;
		this.b = b;
		this.scale = scale;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}

	public float getR() {
		return r;
	}

	public float getG() {
		return g;
	}

	public float getB() {
		return b;
	}

	public float getScale() {
		return scale;
	}

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public float getDz() {
		return dz;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buf) {
		buf.writeFloat(r);
		buf.writeFloat(g);
		buf.writeFloat(b);
		buf.writeFloat(scale);
		buf.writeFloat(dx);
		buf.writeFloat(dy);
		buf.writeFloat(dz);
	}

	@Override
	public String writeToString() {
		return String.format(
				Locale.ROOT,
				"%s %.3f %.3f %.3f %.3f %.3f %.3f %.3f",
				getType().toString(),
				r,
				g,
				b,
				scale,
				dx,
				dy,
				dz
		);
	}

	@Override
	public ParticleType<?> getType() {
		return JerotesParticleTypes.ELEMENT_BOLT.get();
	}
}

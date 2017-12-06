package imeszaros.wav2hex;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteStreams;

public class Converter {

	private final String path;

	public Converter(String path) {
		this.path = path;
	}

	public String convert() {
		try {
			final Path path = Paths.get(this.path);
			final URL url = path.toUri().toURL();
			final AudioInputStream ais = AudioSystem.getAudioInputStream(url);
			final AudioFormat format = ais.getFormat();

			final String result = ByteStreams.readBytes(ais, new ToHexProcessor());

			return String.format("// sample reate: %.2f Hz\n// sample size: %d bits\n// channels: %d\n\n%s\n",
					format.getSampleRate(), format.getSampleSizeInBits(), format.getChannels(), result);

		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static class ToHexProcessor implements ByteProcessor<String> {

		private final List<String> bytes = Lists.newArrayList();

		@Override
		public boolean processBytes(byte[] buf, int off, int len) throws IOException {
			for (int i = off, lim = off + len; i < lim; ++i) {
				bytes.add(String.format("%02x", buf[i]));
			}

			return true;
		}

		@Override
		public String getResult() {
			return Joiner.on(", ").join(bytes);
		}
	}
}
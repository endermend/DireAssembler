package DireAssembler.net.direassembler.mod.common;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
	public static boolean blacklist = true;
	
	public static void synchronizeConfiguration(File configFile) {
		Configuration configuration = new Configuration(configFile);

		blacklist = configuration.getBoolean("Turbo accelerator", Configuration.CATEGORY_GENERAL,
				blacklist, "If true, idk");

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}
}

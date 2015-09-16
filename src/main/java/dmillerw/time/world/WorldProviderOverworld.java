package dmillerw.time.world;

import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.WorldGen.TFCProvider;

import cpw.mods.fml.common.Loader;
import dmillerw.time.data.SessionData;
import net.minecraftforge.common.DimensionManager;

/**
 * @author dmillerw
 */
public class WorldProviderOverworld extends TFCProvider {

	int[] tfcDayLength = {13200,14400,15600,16800,18000,16800,15600,14400,13200,12000,10800,12000};
	float value;
	int cycleTime;
	boolean day;
	boolean initComplete = false;
	
	public static void overrideDefault() {
		DimensionManager.unregisterProviderType(DimensionManager.getProviderType(0));
        DimensionManager.registerProviderType(0, WorldProviderOverworld.class, true);

	}

	@Override
	public float calculateCelestialAngle(long time, float partial) {
		if (!SessionData.modEnabled) {
			return super.calculateCelestialAngle(time, partial);
		}
		
		if (SessionData.tfcSeasons) {
			int intMonth = TFC_Time.currentMonth;
			int dayDuration = tfcDayLength[intMonth];
			int nightDuration = 24000 - dayDuration;
			int absoluteTime = (int) (Math.max(1, time) % (dayDuration + nightDuration));
			if (!initComplete)
			{
				if (absoluteTime != 1){
					return super.calculateCelestialAngle(time, partial);
				}
				else{
					initComplete = true;
				}
			}
			day = absoluteTime >= 0 && absoluteTime < dayDuration;
			int cycleTime = day ? (absoluteTime % dayDuration) : (absoluteTime % nightDuration);
			value = 0.5F * ((float) cycleTime + partial) / (day ? (float)(dayDuration) : (float)(nightDuration));
		}
		else
		{
			int absoluteTime = (int) (Math.max(1, time) % (SessionData.dayDuration + SessionData.nightDuration));
			day = absoluteTime >= 0 && absoluteTime < SessionData.dayDuration;
			int cycleTime = day ? (absoluteTime % SessionData.dayDuration) : (absoluteTime % SessionData.nightDuration);
			value = 0.5F * ((float) cycleTime + partial) / (day ? (float)(SessionData.dayDuration) : (float)(SessionData.nightDuration));
		}

		// This method eventually returns a values from 0 to 1

		// 0 OR 1 has the sun in the center
		// 0.75 is dawn
		// 0.50 has the moon in the center
		// 0.25 is dusk

		// 0.25F is the value size of one pass through the sky

		/*
		if (SessionData.staticAngle >= 0) {
			float value = 0.5F * ((float)SessionData.staticAngle / 180F);
			if (SessionData.staticMoon) {
				value += 0.25F;
			} else {
				value += 0.75F;
			}
			return value;
		}
		*/


		if (day) {
			value += 0.75F;
		} else {
			value += 0.25F;
		}

		return value;
	}
}

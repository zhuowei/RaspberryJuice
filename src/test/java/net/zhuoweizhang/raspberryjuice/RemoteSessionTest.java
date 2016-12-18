package net.zhuoweizhang.raspberryjuice;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.Socket;

import static org.testng.Assert.assertEquals;

public class RemoteSessionTest {

	@Mocked
	private CraftWorld world;

	@Mocked
	private Socket socket;

	private RemoteSession remoteSession;

	private LocationType locationType = LocationType.RELATIVE;

	@BeforeClass
	public void setUp() throws Exception {

		RaspberryJuicePlugin plugin = new MockUp<RaspberryJuicePlugin>() {
			@Mock
			public LocationType getLocationType() {
				return locationType;
			}
		}.getMockInstance();

		new MockUp<RemoteSession>() {
			@Mock
			public void init() {
				//Avoid the real init as it errors out and we don't need it for our tests
			}

		};

		remoteSession = new RemoteSession(plugin, socket);
	}

	/**
	 * Raspberry Pi sets the spawn point to 0,0,0 so values you pass should be the same as you get back
	 */
	@Test
	public void locationToRelativePi() throws Exception {
		Location origin = new Location(world, 0, 0, 0);
		remoteSession.setOrigin(origin);

		double x = 20d;
		double y = 3d;
		double z = -5;
		Location playerPos = new Location(world, x, y, z);
		String location = remoteSession.locationToRelative(playerPos);
		String[] split = location.split(",");
		assertEquals(split[0], "20.0");
		assertEquals(split[1], "3.0");
		assertEquals(split[2], "-5.0");
	}

	@Test
	public void locationToRelativeBukkit() throws Exception {
		Location origin = new Location(world, -100, 50, 100);
		remoteSession.setOrigin(origin);

		double x = 20d;
		double y = 3d;
		double z = -5;
		Location playerPos = new Location(world, x, y, z);
		String location = remoteSession.locationToRelative(playerPos);
		String[] split = location.split(",");
		assertEquals(split[0], "120.0");
		assertEquals(split[1], "-47.0");
		assertEquals(split[2], "-105.0");
	}
}
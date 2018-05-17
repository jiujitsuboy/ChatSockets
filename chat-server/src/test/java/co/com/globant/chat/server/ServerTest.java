package co.com.globant.chat.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.com.globlant.chat.server.Server;
import co.com.globlant.chat.server.SpawnSocketThread;

public class ServerTest {

	@Mock
	private Server server;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void spawnNewSocketThreadTest() {
		final int socketID = 1;
		Socket socket = new Socket();
		Runnable spawnSocketExpected = new SpawnSocketThread(socketID,socket);
		
		when(server.spawnNewSocketThread(any(Socket.class))).thenReturn(spawnSocketExpected);
		
		Runnable spawnSocketResult = server.spawnNewSocketThread(socket);
		
		assertSame(spawnSocketExpected, spawnSocketResult);
	}

	@Test
	public void addSpawnedSocketToDirectoryTest() {
		final int socketID = 1;
		Socket socket = new Socket();
		Runnable spawnSocketExpected = new SpawnSocketThread(socketID,socket);
		
		doNothing().when(server).addSpawnedSocketToDirectory(any(Runnable.class));
		server.addSpawnedSocketToDirectory(spawnSocketExpected);
		
		verify(server,times(1)).addSpawnedSocketToDirectory(any(Runnable.class));
	}
}

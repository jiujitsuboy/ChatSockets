package co.com.globant.chat.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.com.globlant.chat.server.SpawnSocketThread;

public class SpawnSocketThreadTest {

	private static final String MESSAGE = "This is a test";
	
	@Mock
	private SpawnSocketThread spawnSocketThread;	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void SendMessagetest() throws Exception {
		doNothing().when(spawnSocketThread).sendMessage(anyString());
		spawnSocketThread.sendMessage(MESSAGE);
		
		verify(spawnSocketThread,times(1)).sendMessage(anyString());		
	}
	
	@Test
	public void receiveMessagetest() throws Exception {
		when(spawnSocketThread.receiveMessage()).thenReturn(MESSAGE);
		String result  = spawnSocketThread.receiveMessage();		
		assertSame(MESSAGE, result);		
	}
}

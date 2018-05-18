package co.com.globlant.chat;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.com.globlant.chat.client.Client;

public class ClientTest {
	
	private static final String MESSAGE = "This is a test";
	
	@Mock
	private Client client;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void SendMessagetest() throws Exception {
		doNothing().when(client).sendMessage(anyString());
		client.sendMessage(MESSAGE);
		
		verify(client,times(1)).sendMessage(anyString());		
	}
	
	@Test
	public void receiveMessagetest() throws Exception {
		when(client.receiveMessage()).thenReturn(MESSAGE);
		String result  = client.receiveMessage();		
		assertSame(MESSAGE, result);		
	}

}

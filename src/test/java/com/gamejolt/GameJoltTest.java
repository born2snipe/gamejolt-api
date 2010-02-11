/**
 * Copyright to the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.gamejolt;

import com.gamejolt.io.BinarySanitizer;
import com.gamejolt.io.ObjectSerializer;
import com.gamejolt.net.HttpRequest;
import com.gamejolt.net.HttpResponse;
import com.gamejolt.net.RequestFactory;
import com.gamejolt.util.Properties;
import com.gamejolt.util.PropertiesParser;
import com.gamejolt.util.TrophyResponseParser;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class GameJoltTest {
    private RequestFactory requestFactory;
    private HttpRequest request;
    private HttpResponse response;
    private GameJolt gameJolt;
    private TrophyResponseParser trophyResponseParser;
    private PropertiesParser propertiesParser;
    private ObjectSerializer objectSerializer;
    private BinarySanitizer binarySanitizer;
    private Properties properties;
    private static final String RESPONSE_CONTENT = "content";
    private static final byte[] DATA = new byte[0];
    private static final Object OUR_OBJECT = new Object();
    private static final String USERNAME = "username";
    private static final String USER_TOKEN = "userToken";

    @Before
    public void setUp() throws Exception {
        requestFactory = mock(RequestFactory.class);
        request = mock(HttpRequest.class);
        response = mock(HttpResponse.class);
        trophyResponseParser = mock(TrophyResponseParser.class);
        propertiesParser = mock(PropertiesParser.class);
        objectSerializer = mock(ObjectSerializer.class);
        binarySanitizer = mock(BinarySanitizer.class);
        properties = mock(Properties.class);

        gameJolt = new GameJolt(1111, "private-key");
        gameJolt.setRequestFactory(requestFactory);
        gameJolt.setTrophyParser(trophyResponseParser);
        gameJolt.setPropertiesParser(propertiesParser);
        gameJolt.setObjectSerializer(objectSerializer);
        gameJolt.setBinarySanitizer(binarySanitizer);

        when(request.doGet(false)).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.getContentAsString()).thenReturn(RESPONSE_CONTENT);
    }

    @Test
    public void test_loadAllGameData_MultipleKeys() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        when(requestFactory.buildGetGameDataRequest("key1")).thenReturn(request);
        when(requestFactory.buildGetGameDataRequest("key2")).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key1", "key2"));
        when(response.getContentAsString()).thenReturn(RESPONSE_CONTENT, "Success\r\ndata-stored", "Success\r\ndata-stored");
        when(binarySanitizer.unsanitize("data-stored")).thenReturn(DATA);
        when(objectSerializer.deserialize(DATA)).thenReturn(OUR_OBJECT);

        Map<String, Object> data = gameJolt.loadAllGameData();

        assertEquals(2, data.size());
        assertSame(OUR_OBJECT, data.get("key1"));
        assertSame(OUR_OBJECT, data.get("key2"));

        verify(binarySanitizer, times(2)).unsanitize("data-stored");
        verify(objectSerializer, times(2)).deserialize(DATA);
    }

    @Test
    public void test_loadAllUserData_MultipleKeys() {
        hasAVerifiedUser();
        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        when(requestFactory.buildGetUserDataRequest(USERNAME, USER_TOKEN, "key1")).thenReturn(request);
        when(requestFactory.buildGetUserDataRequest(USERNAME, USER_TOKEN, "key2")).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key1", "key2"));
        when(response.getContentAsString()).thenReturn(RESPONSE_CONTENT, "Success\r\ndata-stored", "Success\r\ndata-stored");
        when(binarySanitizer.unsanitize("data-stored")).thenReturn(DATA);
        when(objectSerializer.deserialize(DATA)).thenReturn(OUR_OBJECT);

        Map<String, Object> data = gameJolt.loadAllUserData();

        assertEquals(2, data.size());
        assertSame(OUR_OBJECT, data.get("key1"));
        assertSame(OUR_OBJECT, data.get("key2"));

        verify(binarySanitizer, times(2)).unsanitize("data-stored");
        verify(objectSerializer, times(2)).deserialize(DATA);
    }

    @Test
    public void test_loadAllGameData_SingleKey() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        when(requestFactory.buildGetGameDataRequest("key1")).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key1"));
        when(response.getContentAsString()).thenReturn(RESPONSE_CONTENT, "Success\r\ndata-stored");
        when(binarySanitizer.unsanitize("data-stored")).thenReturn(DATA);
        when(objectSerializer.deserialize(DATA)).thenReturn(OUR_OBJECT);

        Map<String, Object> data = gameJolt.loadAllGameData();

        assertNotNull(data);
        assertEquals(1, data.size());
        assertSame(OUR_OBJECT, data.get("key1"));
    }

    @Test
    public void test_loadAllUserData_SingleKey() {
        hasAVerifiedUser();
        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        when(requestFactory.buildGetUserDataRequest(USERNAME, USER_TOKEN, "key1")).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key1"));
        when(response.getContentAsString()).thenReturn(RESPONSE_CONTENT, "Success\r\ndata-stored");
        when(binarySanitizer.unsanitize("data-stored")).thenReturn(DATA);
        when(objectSerializer.deserialize(DATA)).thenReturn(OUR_OBJECT);

        Map<String, Object> data = gameJolt.loadAllUserData();

        assertNotNull(data);
        assertEquals(1, data.size());
        assertSame(OUR_OBJECT, data.get("key1"));
    }

    @Test
    public void test_loadAllGameData_NoKeys() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(new ArrayList());

        Map<String, Object> data = gameJolt.loadAllGameData();
        assertNotNull(data);
        assertEquals(0, data.size());
        verifyZeroInteractions(binarySanitizer, objectSerializer);
    }

    @Test
    public void test_loadAllUserData_NoKeys() {
        hasAVerifiedUser();
        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(new ArrayList());

        Map<String, Object> data = gameJolt.loadAllUserData();
        assertNotNull(data);
        assertEquals(0, data.size());
        verifyZeroInteractions(binarySanitizer, objectSerializer);
    }

    @Test
    public void test_loadAllUserData_UnverifiedUser() {
        try {
            gameJolt.loadAllUserData();
            fail();
        } catch (UnverifiedUserException err) {

        }

        verifyZeroInteractions(requestFactory, binarySanitizer, objectSerializer);
    }

    @Test
    public void test_getUserData_NoMatchingObject() {
        hasAVerifiedUser();

        when(requestFactory.buildGetUserDataRequest(USERNAME, USER_TOKEN, "key-value")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("FAILURE\nerror message");

        assertNull(gameJolt.getUserData("key-value"));

        verifyZeroInteractions(binarySanitizer, objectSerializer);
    }

    @Test
    public void test_getGameData_NoMatchingObject() {
        when(requestFactory.buildGetGameDataRequest("key-value")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("FAILURE\nerror message");

        assertNull(gameJolt.getGameData("key-value"));

        verifyZeroInteractions(binarySanitizer, objectSerializer);
    }

    @Test
    public void test_getGameData_MatchingObject_WindowsLineEndings() {
        when(requestFactory.buildGetGameDataRequest("key-value")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("Success\r\ndata-stored");
        when(binarySanitizer.unsanitize("data-stored")).thenReturn(DATA);
        when(objectSerializer.deserialize(DATA)).thenReturn(OUR_OBJECT);

        assertSame(OUR_OBJECT, gameJolt.getGameData("key-value"));
    }

    @Test
    public void test_getUserData_MatchingObject_WindowsLineEndings() {
        hasAVerifiedUser();

        when(requestFactory.buildGetUserDataRequest(USERNAME, USER_TOKEN, "key-value")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("Success\r\ndata-stored");
        when(binarySanitizer.unsanitize("data-stored")).thenReturn(DATA);
        when(objectSerializer.deserialize(DATA)).thenReturn(OUR_OBJECT);

        assertSame(OUR_OBJECT, gameJolt.getUserData("key-value"));
    }

    @Test
    public void test_getGameData_MatchingObject_UnixLineEndings() {
        when(requestFactory.buildGetGameDataRequest("key-value")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("Success\ndata-stored");
        when(binarySanitizer.unsanitize("data-stored")).thenReturn(DATA);
        when(objectSerializer.deserialize(DATA)).thenReturn(OUR_OBJECT);

        Object obj = gameJolt.getGameData("key-value");

        assertNotNull(obj);
        assertSame(OUR_OBJECT, obj);
    }

    @Test
    public void test_getUserData_MatchingObject_UnixLineEndings() {
        hasAVerifiedUser();
        when(requestFactory.buildGetUserDataRequest(USERNAME, USER_TOKEN, "key-value")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("Success\ndata-stored");
        when(binarySanitizer.unsanitize("data-stored")).thenReturn(DATA);
        when(objectSerializer.deserialize(DATA)).thenReturn(OUR_OBJECT);

        Object obj = gameJolt.getUserData("key-value");

        assertNotNull(obj);
        assertSame(OUR_OBJECT, obj);
    }

    @Test
    public void test_getUserData_UnverifiedUser() {
        try {
            gameJolt.getUserData("key-value");
            fail();
        } catch (UnverifiedUserException err) {

        }
    }

    @Test
    public void test_clearAllGameData_MultipleKeys() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        when(requestFactory.buildRemoveGameDataRequest("key-value")).thenReturn(request);
        when(requestFactory.buildRemoveGameDataRequest("key-value2")).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key-value", "key-value2"));
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
        when(properties.getBoolean("success")).thenReturn(true);

        gameJolt.clearAllGameData();

        verify(requestFactory).buildRemoveGameDataRequest("key-value");
        verify(requestFactory).buildRemoveGameDataRequest("key-value2");
    }


    @Test
    public void test_clearAllGameData_SingleKey() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        when(requestFactory.buildRemoveGameDataRequest("key-value")).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key-value"));
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
        when(properties.getBoolean("success")).thenReturn(true);

        gameJolt.clearAllGameData();

        verify(requestFactory).buildRemoveGameDataRequest("key-value");
    }

    @Test
    public void test_clearAllGameData_NoKeys() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(new ArrayList());

        gameJolt.clearAllGameData();

        verify(requestFactory).buildGameDataKeysRequest();
        verifyNoMoreInteractions(requestFactory);
    }

    @Test
    public void test_getGameDataKeys() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key-value"));

        assertEquals(Arrays.asList("key-value"), gameJolt.getGameDataKeys());
    }

    @Test
    public void test_clearAllUserData_MultipleKeys() {
        hasAVerifiedUser();
        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        when(requestFactory.buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value")).thenReturn(request);
        when(requestFactory.buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value2")).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key-value", "key-value2"));
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
        when(properties.getBoolean("success")).thenReturn(true);

        gameJolt.clearAllUserData();

        verify(requestFactory).buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value");
        verify(requestFactory).buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value2");
    }

    @Test
    public void test_clearAllUserData_SingleKey() {
        hasAVerifiedUser();
        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        when(requestFactory.buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value")).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key-value"));
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
        when(properties.getBoolean("success")).thenReturn(true);

        gameJolt.clearAllUserData();

        verify(requestFactory).buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value");
    }

    @Test
    public void test_clearAllUserData_NoKeys() {
        hasAVerifiedUser();
        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(new ArrayList());

        gameJolt.clearAllUserData();

        verify(requestFactory).buildVerifyUserRequest(USERNAME, USER_TOKEN);
        verify(requestFactory).buildUserDataKeysRequest(USERNAME, USER_TOKEN);
        verifyNoMoreInteractions(requestFactory);
    }

    @Test
    public void test_getUserDataKeys() {
        hasAVerifiedUser();
        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        when(propertiesParser.parseToList(RESPONSE_CONTENT, "key")).thenReturn(Arrays.asList("key-value"));

        assertEquals(Arrays.asList("key-value"), gameJolt.getUserDataKeys());
    }

    @Test
    public void test_getGameDataKeys_NoKeys() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        Properties properties = new Properties();
        properties.put("success", "true");
        properties.put("key", "");
        when(propertiesParser.parse(RESPONSE_CONTENT)).thenReturn(Arrays.asList(properties));

        assertEquals(Arrays.asList(), gameJolt.getGameDataKeys());
    }

    @Test
    public void test_getUserDataKeys_NoKeys() {
        hasAVerifiedUser();
        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        Properties properties = new Properties();
        properties.put("success", "true");
        properties.put("key", "");
        when(propertiesParser.parse(RESPONSE_CONTENT)).thenReturn(Arrays.asList(properties));

        assertEquals(Arrays.asList(), gameJolt.getUserDataKeys());
    }

    @Test
    public void test_getGameDataKeys_Failed() {
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(request);
        Properties properties = new Properties();
        properties.put("success", "false");
        when(propertiesParser.parse(RESPONSE_CONTENT)).thenReturn(Arrays.asList(properties));

        assertEquals(Arrays.asList(), gameJolt.getGameDataKeys());
    }

    @Test
    public void test_getUserDataKeys_Failed() {
        hasAVerifiedUser();

        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        Properties properties = new Properties();
        properties.put("success", "false");
        when(propertiesParser.parse(RESPONSE_CONTENT)).thenReturn(Arrays.asList(properties));

        assertEquals(Arrays.asList(), gameJolt.getUserDataKeys());
    }

    @Test
    public void test_getUserDataKeys_Unverified() {
        try {
            gameJolt.getUserDataKeys();
            fail();
        } catch (UnverifiedUserException err) {

        }
    }

    @Test
    public void test_removeGameData() {
        when(requestFactory.buildRemoveGameDataRequest("name")).thenReturn(request);
        receivesResponse(response, true);

        assertTrue(gameJolt.removeGameData("name"));
    }

    @Test
    public void test_removeGameData_Failed() {
        when(requestFactory.buildRemoveGameDataRequest("name")).thenReturn(request);
        receivesResponse(response, false);

        assertFalse(gameJolt.removeGameData("name"));
    }

    @Test
    public void test_removeUserData_Failed() {
        hasAVerifiedUser();

        when(requestFactory.buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "name")).thenReturn(request);
        receivesResponse(response, false);

        assertFalse(gameJolt.removeUserData("name"));
    }

    @Test
    public void test_removeUserData() {
        hasAVerifiedUser();

        when(requestFactory.buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "name")).thenReturn(request);
        receivesResponse(response, true);

        assertTrue(gameJolt.removeUserData("name"));
    }

    @Test
    public void test_removeUserData_UserNotVerified() {
        try {
            gameJolt.removeUserData("name");
            fail();
        } catch (UnverifiedUserException e) {

        }
    }


    @Test
    public void test_storeGameData_NullObjectGiven() {
        try {
            gameJolt.storeGameData("name", (Object) null);
            fail();
        } catch (NullPointerException err) {
            assertEquals("You supplied a null object for storing. This is invalid, if you would like to remove data, please use the removeGameData method", err.getMessage());
        }
    }

    @Test
    public void test_storeUserData_NullObjectGiven() {
        hasAVerifiedUser();

        try {
            gameJolt.storeUserData("name", (Object) null);
            fail();
        } catch (NullPointerException err) {
            assertEquals("You supplied a null object for storing. This is invalid, if you would like to remove data, please use the removeUserData method", err.getMessage());
        }
    }

    @Test
    public void test_storeUserData_ObjectSerializerReturnsANullByteArray() {
        DummyObject obj = new DummyObject();

        hasAVerifiedUser();

        Properties properties = new Properties();
        properties.put("success", "true");

        when(requestFactory.buildStoreUserDataRequest(USERNAME, USER_TOKEN, "name", "sanitized-data")).thenReturn(request);
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
        when(objectSerializer.serialize(obj)).thenReturn(null);

        try {
            gameJolt.storeUserData("name", obj);
            fail();
        } catch (NullPointerException err) {
            assertEquals("ObjectSerializer serialized " + DummyObject.class + " to a null byte array, please give at least an empty byte array", err.getMessage());
        }
    }

    @Test
    public void test_storeGameData_ObjectSerializerReturnsANullByteArray() {
        DummyObject obj = new DummyObject();

        Properties properties = new Properties();
        properties.put("success", "true");

        when(requestFactory.buildStoreGameDataRequest("name", "sanitized-data")).thenReturn(request);
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
        when(objectSerializer.serialize(obj)).thenReturn(null);

        try {
            gameJolt.storeGameData("name", obj);
            fail();
        } catch (NullPointerException err) {
            assertEquals("ObjectSerializer serialized " + DummyObject.class + " to a null byte array, please give at least an empty byte array", err.getMessage());
        }
    }

    @Test
    public void test_storeUserData_Object() {
        DummyObject obj = new DummyObject();
        byte[] data = new byte[0];

        hasAVerifiedUser();

        Properties properties = new Properties();
        properties.put("success", "true");

        when(requestFactory.buildStoreUserDataRequest(USERNAME, USER_TOKEN, "name", "sanitized-data")).thenReturn(request);
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
        when(objectSerializer.serialize(obj)).thenReturn(data);
        when(binarySanitizer.sanitize(data)).thenReturn("sanitized-data");

        assertTrue(gameJolt.storeUserData("name", obj));
    }

    @Test
    public void test_storeGameData_Object() {
        DummyObject obj = new DummyObject();
        byte[] data = new byte[0];

        Properties properties = new Properties();
        properties.put("success", "true");

        when(requestFactory.buildStoreGameDataRequest("name", "sanitized-data")).thenReturn(request);
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
        when(objectSerializer.serialize(obj)).thenReturn(data);
        when(binarySanitizer.sanitize(data)).thenReturn("sanitized-data");

        assertTrue(gameJolt.storeGameData("name", obj));
    }

    @Test
    public void test_getUnachievedTrophies_UnverifiedUser() {
        try {
            gameJolt.getUnachievedTrophies();
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getUnachievedTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser();

        when(requestFactory.buildTrophiesRequest(USERNAME, USER_TOKEN, "false")).thenReturn(request);
        when(trophyResponseParser.parse(RESPONSE_CONTENT)).thenReturn(trophies);

        assertSame(trophies, gameJolt.getUnachievedTrophies());
    }

    @Test
    public void test_getAchievedTrophies_UnverifiedUser() {
        try {
            gameJolt.getAchievedTrophies();
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getAchievedTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser();

        when(requestFactory.buildTrophiesRequest(USERNAME, USER_TOKEN, "true")).thenReturn(request);
        when(trophyResponseParser.parse(RESPONSE_CONTENT)).thenReturn(trophies);

        assertSame(trophies, gameJolt.getAchievedTrophies());
    }

    @Test
    public void test_getAllTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser();

        when(requestFactory.buildTrophiesRequest(USERNAME, USER_TOKEN, "empty")).thenReturn(request);
        when(trophyResponseParser.parse(RESPONSE_CONTENT)).thenReturn(trophies);

        assertSame(trophies, gameJolt.getAllTrophies());
    }

    @Test
    public void test_getAllTrophies_UnverifiedUser() {
        try {
            gameJolt.getAllTrophies();
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getTrophy_Unverified() {
        try {
            gameJolt.getTrophy(12);
            fail();
        } catch (UnverifiedUserException er) {

        }
    }

    @Test
    public void test_getTrophy_NoMatchingTrophy() {
        hasAVerifiedUser();

        when(requestFactory.buildTrophyRequest(USERNAME, USER_TOKEN, "12")).thenReturn(request);
        when(trophyResponseParser.parse(RESPONSE_CONTENT)).thenReturn(new ArrayList());

        assertNull(gameJolt.getTrophy(12));
    }

    @Test
    public void test_getTrophy() throws MalformedURLException {
        Trophy trophy = new Trophy();

        hasAVerifiedUser();

        when(requestFactory.buildTrophyRequest(USERNAME, USER_TOKEN, "12")).thenReturn(request);
        when(trophyResponseParser.parse(RESPONSE_CONTENT)).thenReturn(Arrays.asList(trophy));

        assertSame(trophy, gameJolt.getTrophy(12));
    }

    @Test
    public void test_achievedTrophy_UnverifiedUser() {
        try {
            gameJolt.achievedTrophy(1234);
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_achievedTrophy_VerifiedUser() {
        hasAVerifiedUser();

        when(requestFactory.buildAchievedTrophyRequest(USERNAME, USER_TOKEN, "1234")).thenReturn(request);
        receivesResponse(response, true);

        assertTrue(gameJolt.achievedTrophy(1234));
    }

    @Test
    public void test_achievedTrophy_AlreadyAchieved() {
        hasAVerifiedUser();

        when(requestFactory.buildAchievedTrophyRequest(USERNAME, USER_TOKEN, "1234")).thenReturn(request);
        receivesResponse(response, false);

        assertFalse(gameJolt.achievedTrophy(1234));
    }

    @Test
    public void test_verifyUser_NotVerified() {
        when(requestFactory.buildVerifyUserRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        receivesResponse(response, false);

        assertFalse(gameJolt.verifyUser(USERNAME, USER_TOKEN));
    }

    @Test
    public void test_verifyUser_Verified() {
        when(requestFactory.buildVerifyUserRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        receivesResponse(response, true);

        assertTrue(gameJolt.verifyUser(USERNAME, USER_TOKEN));
    }

    @Test
    public void test_verifyUser_BadResponse() {
        when(response.isSuccessful()).thenReturn(false);
        when(response.getCode()).thenReturn(500);
        when(requestFactory.buildVerifyUserRequest(USERNAME, USER_TOKEN)).thenReturn(request);

        try {
            gameJolt.verifyUser(USERNAME, USER_TOKEN);
            fail();
        } catch (GameJoltException err) {
            assertEquals("Bad Http Response received response code 500", err.getMessage());
        }
    }

    @Test
    public void test_verifyUser_AlreadyVerified() {
        when(requestFactory.buildVerifyUserRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        receivesResponse(response, true);

        assertTrue(gameJolt.verifyUser(USERNAME, USER_TOKEN));
        assertTrue(gameJolt.verifyUser(USERNAME, USER_TOKEN));

        verify(requestFactory, times(1)).buildVerifyUserRequest(USERNAME, USER_TOKEN);
    }

    @Test
    public void test_verifyUser_DifferentUsernameAndFails() {
        hasAVerifiedUser();

        when(requestFactory.buildVerifyUserRequest("different", USER_TOKEN)).thenReturn(request);
        receivesResponse(response, false);

        assertFalse(gameJolt.verifyUser("different", USER_TOKEN));
    }

    @Test
    public void test_verifyUser_DifferentUserTokenAndFails() {
        hasAVerifiedUser();

        when(requestFactory.buildVerifyUserRequest(USERNAME, "differentUserToken")).thenReturn(request);
        receivesResponse(response, false);

        assertFalse(gameJolt.verifyUser(USERNAME, "differentUserToken"));
    }

    private void hasAVerifiedUser() {
        HttpRequest httpRequest = mock(HttpRequest.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(requestFactory.buildVerifyUserRequest(USERNAME, USER_TOKEN)).thenReturn(httpRequest);
        when(httpRequest.doGet(false)).thenReturn(httpResponse);
        when(httpResponse.isSuccessful()).thenReturn(true);
        receivesResponse(httpResponse, true);

        assertTrue(gameJolt.verifyUser(USERNAME, USER_TOKEN));
    }

    private void receivesResponse(HttpResponse httpResponse, boolean successful) {
        when(httpResponse.getContentAsString()).thenReturn(RESPONSE_CONTENT);
        Properties properties = new Properties();
        properties.put("success", String.valueOf(successful));
        when(propertiesParser.parseProperties(RESPONSE_CONTENT)).thenReturn(properties);
    }
}

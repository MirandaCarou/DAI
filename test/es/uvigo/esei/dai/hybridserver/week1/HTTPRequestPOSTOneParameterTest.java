/**
 *  HybridServer
 *  Copyright (C) 2022 Miguel Reboiro-Jato
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver.week1;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.junit.Assert.assertThat;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;

public class HTTPRequestPOSTOneParameterTest {
	private String requestText;
	private HTTPRequest request;

	@Before
	public void setUp() throws Exception {
		this.requestText = "POST / HTTP/1.1\r\n" +
			"Host: localhost\r\n" +
			"Content-Length: 21\r\n\r\n" +
			"message=Hello world!!";

		this.request = new HTTPRequest(new StringReader(this.requestText));
	}

	@Test
	public final void testGetMethod() {
		assertThat(request.getMethod(), is(equalTo(HTTPRequestMethod.POST)));
	}

	@Test
	public final void testGetResourceChain() {
		assertThat(request.getResourceChain(), is(equalTo("/")));
	}

	@Test
	public final void testGetResourcePath() {
		assertThat(request.getResourcePath(), is(emptyArray()));
	}

	@Test
	public final void testGetResourceName() {
		assertThat(request.getResourceName(), is(emptyString()));
	}

	@Test
	public final void testGetHttpVersion() {
		assertThat(request.getHttpVersion(), is(equalTo(HTTPHeaders.HTTP_1_1.getHeader())));
	}

	@Test
	public final void testGetResourceParameters() {
		assertThat(request.getResourceParameters(), hasEntry("message", "Hello world!!"));
		assertThat(request.getResourceParameters(), is(aMapWithSize(1)));
	}

	@Test
	public final void testGetHeaderParameters() {
		assertThat(request.getHeaderParameters(), allOf(
			hasEntry("Host", "localhost"),
			hasEntry("Content-Length", "21")
		));
		assertThat(request.getHeaderParameters(), is(aMapWithSize(2)));
	}

	@Test
	public final void testGetContent() {
		assertThat(request.getContent(), is(equalTo("message=Hello world!!")));
	}

	@Test
	public final void testGetContentLength() {
		assertThat(request.getContentLength(), is(equalTo(21)));
	}

	@Test
	public final void testToString() {
		assertThat(request.toString(), is(equalTo(requestText)));
	}

}

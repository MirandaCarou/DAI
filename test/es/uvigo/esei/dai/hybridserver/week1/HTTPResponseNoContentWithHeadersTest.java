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

import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HTTPResponseNoContentWithHeadersTest {
	private HTTPResponse response;

	@Before
	public void setUp() throws Exception {
		this.response = new HTTPResponse();

		this.response.setStatus(HTTPResponseStatus.S200);
		this.response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
		this.response.putParameter("Content-Type", "text/html");
		this.response.putParameter("Content-Encoding", "deflate");
		this.response.putParameter("Content-Language", "en");
	}

	@Test
	public final void testPrint() throws IOException {
		try (final StringWriter writer = new StringWriter()) {
			this.response.print(writer);

			final String responseText = writer.toString();
			assertThat(responseText, startsWith("HTTP/1.1 200 OK"));
			assertThat(responseText, containsString("Content-Type: text/html"));
			assertThat(responseText, containsString("Content-Encoding: deflate"));
			assertThat(responseText, containsString("Content-Language: en"));
			assertThat(responseText, endsWith("\r\n\r\n"));
		}
	}
}

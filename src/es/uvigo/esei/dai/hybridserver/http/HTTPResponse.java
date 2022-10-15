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
package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
	HTTPResponseStatus status;
	String version;
	String content;
	LinkedHashMap<String, String> parameters;

	public HTTPResponse() {
		version = "";
		content = "";
		parameters = new LinkedHashMap<>();
	}

	public HTTPResponseStatus getStatus() {
		return status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String putParameter(String name, String value) {
		return parameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return parameters.containsKey(name);
	}

	public String removeParameter(String name) {
		return parameters.remove(name);
	}

	public void clearParameters() {
		parameters.clear();
	}

	public List<String> listParameters() {
		List<String> listParameters = new ArrayList<>(parameters.keySet());
		return listParameters;
	}

	public void print(Writer writer) throws IOException {
		List<String> parametersKey = listParameters();
		writer.write(version + " ");
		writer.write(status.getCode() + " ");
		writer.write(status.getStatus());
		if (!parameters.isEmpty()) {
			writer.write("\r\n");
			for (int i = 0; i < parameters.size(); i++) {
				writer.write(parametersKey.get(i) + ": " + parameters.get(parametersKey.get(i)) + "\r\n");
			}
		} else {
			writer.write("\r\n");
		}
		if (!content.isEmpty()) {
			writer.write("Content-Length: " + content.length());
			writer.write("\r\n\r\n" + content);
		} else {
			writer.write("\r\n");

		}
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}

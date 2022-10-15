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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {
	private ArrayList<String> requestLines;
	private BufferedReader request;
	private LinkedHashMap<String, String> resourcesParameters;
	private LinkedHashMap<String, String> headerParameters;
	private String[] resourcePath = {};

	private String method;
	private String resourceChain;
	private String resourceName;
	private String HTTPVersion;
	private int contentSize;
	private String content;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		// Try con resources???
		requestLines = new ArrayList<String>();
		request = new BufferedReader(reader);
		resourcesParameters = new LinkedHashMap<String, String>();
		headerParameters = new LinkedHashMap<String, String>();
		resourceName = "";
		parseRequest(request);
	}

	public void parseRequest(BufferedReader request) throws IOException, HTTPParseException {
		String line;
		boolean missingMethod = false;
		boolean missingFirstLine = false;
		boolean missingResource = false;
		boolean missingVersion = false;

		try {
			while ((line = request.readLine()) != null) {
				requestLines.add(line);
			}

			String firstLine = requestLines.get(0);

			String[] firstLineElements = firstLine.split(" ");
			firstLineElements = cleanArray(firstLineElements);

			if (firstLineElements.length != 3) {
				// checking if there is a method, if method is null, throw exception
				method = firstLineElements[0].trim();

				if (getMethod() == null) {
					missingMethod = true;
					method = "";
				} else {
					method = firstLineElements[0].trim();
				}

				if (firstLineElements.length == 2) {

					if (((!method.isEmpty()) && firstLineElements[1].trim().equals("HTTP/1.1\r\n"))) {
						missingResource = true;
					}

					if (((!method.isEmpty()) && (!firstLineElements[1].trim().equals("HTTP/1.1\r\n")))) {
						missingVersion = true;
					}

				} else if ((firstLineElements.length == 1)) {
					if (!firstLineElements[0].trim().equals("HTTP/1.1\r\n")) {
						missingVersion = true;
					} else if ((!method.isEmpty())) {
						missingResource = true;
					} else {
						missingMethod = true;
					}

				}

				// for us to have a missing first line we need not to have an method,
				// resource_path and httversion
				if (missingMethod && missingResource && missingVersion) {
					missingFirstLine = true;
				}

				if (missingFirstLine) {
					throw new HTTPParseException(" MISSING FISRT LINE");

				} else if (missingMethod) {
					throw new HTTPParseException("MISSING METHOD");

				} else if (missingResource) {
					throw new HTTPParseException("MISSING RESOURCE");

				} else if (missingVersion) {
					throw new HTTPParseException("MISSING VERSION");

				}

			} else {
				method = firstLineElements[0].trim();
				resourceChain = firstLineElements[1].trim();
				HTTPVersion = firstLineElements[2].trim();

				if (method.equals("POST")) {
					parseResoucersParametersPOST();
				} else {
					parseResourcesParameters();
				}

				parameterParser();
				contentLength();
			}

		} catch (IOException e) {
			System.err.println("FAIL PARSING REQUEST: parseRequest");
			e.printStackTrace();
		}

	}

	public String[] cleanArray(String[] w) {
		for (int i = 0; i < w.length; i++) {
			if (!w[i].trim().equals("") || w[i] != null) {
				w[i] = w[i].trim();
			}
		}
		return w;

	}

	// Parse resources for post request
	public void parseResoucersParametersPOST() throws UnsupportedEncodingException {
		int i = 0;
		ArrayList<String> content = new ArrayList<String>();
		String[] parameters;

		if (resourceChain.length() > 1) {
			resourceName = resourceChain.substring(1);
			resourcePath = resourceName.split("/");
		}

		// get the line before the content
		while (!requestLines.get(i).isEmpty()) {
			i++;
		}

		for (int j = i + 1; j < requestLines.size(); j++) {
			content.add(requestLines.get(j));
		}

		String decoded = URLDecoder.decode(content.get(0), StandardCharsets.UTF_8.toString());

		parameters = decoded.split("&");

		for (int z = 0; z < parameters.length; z++) {
			String[] temp = parameters[z].split("=");
			resourcesParameters.put(temp[0], temp[1]);
		}
	}

	// parse resources for any request
	public void parseResourcesParameters() {
		if (resourceChain.length() > 1) {

			String[] resourceArray = resourceChain.split("\\?");
			resourceName = resourceArray[0].substring(1);

			resourcePath = resourceName.split("/");

			if (resourceArray.length > 1) {

				String[] parameters = resourceArray[1].split("&");

				for (int i = 0; i < parameters.length; i++) {
					String[] temp = parameters[i].split("=");
					resourcesParameters.put(temp[0], temp[1]);
				}

			}
		}
	}

	public void parameterParser() throws HTTPParseException {
		ArrayList<String> parametersParser = new ArrayList<>();
		boolean stop = true;

		// reads all parameters and stops before content
		for (int i = 1; i < requestLines.size(); i++) {
			if (!(requestLines.get(i).isEmpty()) && stop) {
				String prueba = requestLines.get(i);
				parametersParser.add(prueba);
			} else {
				stop = false;
			}
		}
		if (parametersParser.isEmpty()) {
			throw new HTTPParseException("INVALID HEADER");
		} else {
			// fills out a map with the parameters
			for (int j = 0; j < parametersParser.size(); j++) {
				String[] temp = parametersParser.get(j).split(": ");
				temp = cleanArray(temp);
				if (temp.length != 2) {
					throw new HTTPParseException("INVALID HEADER");
				}
				if ((!temp[0].isEmpty()) && (!temp[1].isEmpty())) {

					headerParameters.put(temp[0].trim(), temp[1].trim());

				}

			}
		}

	}

	public void contentLength() throws UnsupportedEncodingException {
		String contentString = new String();
		int tam = 0;
		int numLine = 0;

		while (!requestLines.get(numLine).isEmpty()) {
			numLine++;
		}

		for (int j = numLine + 1; j < requestLines.size(); j++) {
			contentString = contentString + requestLines.get(j);
		}

		if (method.equals("POST")) {

			String decoded = URLDecoder.decode(contentString, StandardCharsets.UTF_8.toString());

			content = decoded;
			tam = contentString.length();

		} else {
			if (contentString.length() > 0) {
				content = contentString;
				tam = contentString.length();
			} else {
				content = null;
			}

		}

		contentSize = tam;
	}

	public HTTPRequestMethod getMethod() {
		if (method.equals("HEAD")) {
			return HTTPRequestMethod.HEAD;
		} else if (method.equals("GET")) {
			return HTTPRequestMethod.GET;
		} else if (method.equals("POST")) {
			return HTTPRequestMethod.POST;
		} else if (method.equals("PUT")) {
			return HTTPRequestMethod.PUT;
		} else if (method.equals("DELETE")) {
			return HTTPRequestMethod.DELETE;
		} else if (method.equals("TRACE")) {
			return HTTPRequestMethod.TRACE;
		} else if (method.equals("OPTIONS")) {
			return HTTPRequestMethod.OPTIONS;
		} else if (method.equals("CONNECT")) {
			return HTTPRequestMethod.CONNECT;
		} else {
			return null;
		}
	}

	public String getResourceChain() {
		return resourceChain;
	}

	public String[] getResourcePath() {
		return resourcePath;
	}

	public String getResourceName() {
		return resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return resourcesParameters;
	}

	public String getHttpVersion() {
		return HTTPVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return headerParameters;
	}

	public String getContent() {
		return content;
	}

	public int getContentLength() {
		return contentSize;
	}

	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");
		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();

	}
}

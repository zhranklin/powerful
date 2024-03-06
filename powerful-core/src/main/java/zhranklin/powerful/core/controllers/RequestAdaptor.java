package zhranklin.powerful.core.controllers;


import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class RequestAdaptor {

	private static boolean isJakarta = true;

	private final NativeWebRequest request;

	static {
		try {
			ClassUtils.forName("jakarta.servlet.http.HttpServletRequest", RequestAdaptor.class.getClassLoader());
		} catch (ClassNotFoundException ex) {
			isJakarta = false;
		}
	}

	public RequestAdaptor(NativeWebRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		if (isJakarta) {
			jakarta.servlet.http.HttpServletRequest r = request.getNativeRequest(jakarta.servlet.http.HttpServletRequest.class);
			return new HttpServletRequest() {
				@Override
				public String getMethod() {
					return r.getMethod();
				}

				@Override
				public String getServletPath() {
					return r.getServletPath();
				}

				@Override
				public Enumeration<String> getHeaderNames() {
					return r.getHeaderNames();
				}

				@Override
				public Enumeration<String> getHeaders(String name) {
					return r.getHeaders(name);
				}

				@Override
				public Object getAttribute(String key) {
					return r.getAttribute(key);
				}
			};
		} else {
			javax.servlet.http.HttpServletRequest r = request.getNativeRequest(javax.servlet.http.HttpServletRequest.class);
			return new HttpServletRequest() {
				@Override
				public String getMethod() {
					return r.getMethod();
				}

				@Override
				public String getServletPath() {
					return r.getServletPath();
				}

				@Override
				public Enumeration<String> getHeaderNames() {
					return r.getHeaderNames();
				}

				@Override
				public Enumeration<String> getHeaders(String name) {
					return r.getHeaders(name);
				}

				@Override
				public Object getAttribute(String key) {
					return r.getAttribute(key);
				}
			};
		}
	}

	public HttpServletResponse getResponse() {
		if (isJakarta) {
			jakarta.servlet.http.HttpServletResponse r = request.getNativeResponse(jakarta.servlet.http.HttpServletResponse.class);
			return new HttpServletResponse() {
				@Override
				public PrintWriter getWriter() throws IOException {
					return r.getWriter();
				}

				@Override
				public void flushBuffer() throws IOException {
					r.flushBuffer();
				}
			};
		} else {
			javax.servlet.http.HttpServletResponse r = request.getNativeRequest(javax.servlet.http.HttpServletResponse.class);
			return new HttpServletResponse() {
				@Override
				public PrintWriter getWriter() throws IOException {
					return r.getWriter();
				}

				@Override
				public void flushBuffer() throws IOException {
					r.flushBuffer();
				}
			};
		}

	}


	public interface HttpServletResponse {

		PrintWriter getWriter() throws IOException;

		void flushBuffer() throws IOException;
		// 可以直接在这里新增代理方法
	}
	public interface HttpServletRequest {

		String getMethod();

		String getServletPath();

		Enumeration<String> getHeaderNames();

		Enumeration<String> getHeaders(String name);

		Object getAttribute(String key);
		// 可以直接在这里新增代理方法
	}



}
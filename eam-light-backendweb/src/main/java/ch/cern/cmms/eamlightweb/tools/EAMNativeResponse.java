package ch.cern.cmms.eamlightweb.tools;

import ch.cern.eam.wshub.core.tools.InforException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EAMNativeResponse<T> {

	private T data;

	private List<Error> errors;

	private EAMNativeResponse() {
	}

	private EAMNativeResponse(T data, List<Error> errors) {
		this.data = data;
		this.errors = errors;
	}

	public static<T> EAMNativeResponse fromData(T data) {
		return new EAMNativeResponse(data, new ArrayList<>());
	}

	public static EAMNativeResponse fromException(Exception exception) {
		return new EAMNativeResponse(null, buildErrorsList(exception));
	}

	public EAMNativeResponse(List<Error> errors) {
		this.errors = errors;
	}


	/**
	 * Create Error list from an exception
	 *
	 * @param exception
	 *            The exception that will become a list of errors
	 * @return The list of errors
	 */
	private static List<Error> buildErrorsList(Exception exception) {
		if (exception instanceof InforException
			&& ((InforException)exception).getExceptionInfoList() != null) {
			InforException inforException = (InforException) exception;
			return Arrays.asList(inforException.getExceptionInfoList())
					.stream()
					.map(error -> new Error("", error.getLocation(), error.getMessage(), error.getName()))
					.collect(Collectors.toList());
		}
		else {
			// Just the message
			List<Error> errorList = new ArrayList<>();
			errorList.add(new Error("", "", exception.getMessage()));
			return errorList;
		}

	}

	//TODO @JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Error {

		private String code;

		private String location;

		private String message;

		private String name;

		public Error(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public Error(String code, String location, String message) {
			this(code, message);
			this.location = location;
		}

		public Error(String code, String location, String message, String name) {
			this(code, location, message);
			this.name = name;
		}

		@JsonProperty("Code")
		public String getCode() {
			return code;
		}

		@JsonProperty("Location")
		public String getLocation() {
			return location;
		}

		@JsonProperty("Message")
		public String getMessage() {
			return message;
		}

		@JsonProperty("Name")
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Error{" +
					"code='" + code + '\'' +
					", location='" + location + '\'' +
					", message='" + message + '\'' +
					'}';
		}
	}

	@JsonProperty("Result")
	public T getData() {
		return data;
	}

	@JsonProperty("ErrorAlert")
	public List<Error> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return "EAMResponse [" + (data != null ? "data=" + data + ", " : "")
				+ (errors != null ? "errors=" + errors : "") + "]";
	}

}

package com.example.http;

public class ServerUtil {

	private ServerUtil() {
	}

	public static String getFileExt(final String path) {
		int slashIndex = path.lastIndexOf(ServerConstant.FORWARD_SINGLE_SLASH);
		String basename = (slashIndex < 0) ? path : path.substring(slashIndex + 1);

		int dotIndex = basename.lastIndexOf('.');
		if (dotIndex >= 0) {
			return basename.substring(dotIndex + 1);
		} else {
			return "";
		}
	}

	public static String getFileMime(final String path) {
		String ext = getFileExt(path).toLowerCase();

		return ServerConstant.MIME_MAP.getOrDefault(ext, ServerConstant.APPLICATION_OCTET_STREAM);
	}

}
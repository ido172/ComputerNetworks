
public class FileTypeToContentType {

	public static String convert(String fileName) {
		String fileType = getFileType(fileName);
		String contentType = null;
		
		if (fileType.equals("html") || fileType.equals("txt")) 
		{
			contentType = HttpHeaders.CONTENT_TYPE_HTML;
		} 
		else if (fileType.equals("bmp") || fileType.equals("gif")
				|| fileType.equals("jpg") || fileType.equals("png"))
		{
			contentType = HttpHeaders.CONTENT_TYPE_IMAGE;
		} 
		else if (fileType.equals("ico")) 
		{
			contentType = HttpHeaders.CONTENT_TYPE_ICON;
		} 
		else 
		{
			contentType = HttpHeaders.CONTENT_TYPE_APP;
		}
		
		return contentType;
	}

	private static String getFileType(String fileName) {
		String fileType = "";
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot > -1) {
			fileType = fileName.substring(lastDot + 1, fileName.length()).toLowerCase();
		}
		
		return fileType; 
	}

}

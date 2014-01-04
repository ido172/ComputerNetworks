public class HTMLCreator {

	private static String DATA_PLACEHOLDER = "[DATA_PLACEHOLDER]";

	public static String createRemainderPage(String userName, String htmlTemaplate) {
		String tr = "<tr><td>bla bla</td><td>ido</td><td>kuku</td></tr>";
		return htmlTemaplate.replace(DATA_PLACEHOLDER, tr + tr + tr);
	}

}

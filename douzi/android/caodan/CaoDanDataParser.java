/**
 *  @author douzifly@gmail.com
 *  @date 2011-9-23
 */
package douzi.android.caodan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author douzifly@gmail.com
 * 
 */
public class CaoDanDataParser {

	public ArrayList<String> getCaoDanData(String htmlPage) {
		ArrayList<String> datas = null;

		if (htmlPage != null && htmlPage.length() > 0) {
			String regExp = "t.><a .*?>(.*?)</a>";

			Pattern pattern = Pattern.compile(regExp, Pattern.DOTALL | Pattern.MULTILINE);
			Matcher match = pattern.matcher(htmlPage);
			while(match.find()){
				if(datas == null){
					datas = new ArrayList<String>(10);
				}
				datas.add(match.group(1));
			}

		}
		return datas;
	}

}

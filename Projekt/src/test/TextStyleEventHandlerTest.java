package test;

import static org.junit.Assert.*;

import org.junit.Test;


public class TextStyleEventHandlerTest {

	@Test
	public void testPreceededTagIndices() {
		String a = "<body>HEJSAN <b>JAG</b> &lt;b&gt;ÄR&lt;b&gt; ANTON";
		String b = "HEJSAN JAG <b>ÄR<b> ANTON";
		//assert(p(b,a,b.length()) ==  25);
		//assert(p("HEJSAN JAG ÄR ANTON","HEJSAN <b>JAG</b> ÄR ANTON","HEJSAN JAG ÄR ANTON".length()) == 7);
		//assert(p("HEJSAN","<body>HEJSAN</body>","HEJSAN".length()) == 6);
		
		//assert(p("-öljbh okjljö jbkljökkjb bö","<body>\n-öljbh okjljö jbkljökkjb bö\n</body>", 25) == 6);
		assert(p("HEJSAN","<body>HEJSAN",0) == 6);
	}
	
	
	public int p(String plain, String html, int endIndex) {			
		
		int pl = 0;
		int ht = 0;
		if(endIndex == 0) {
			endIndex++;
		}
		while (pl < endIndex) {
			//System.out.println("Index at p:" + plain.charAt(pl) + " h:" + html.charAt(ht));
			if(plain.charAt(pl) == html.charAt(ht)) {
				pl++;
				ht++;
			} else if (html.charAt(ht) == '<') {
				while (html.charAt(ht) != '>') {
					ht++;
				}
				ht++;
			} else if (html.charAt(ht) == '&') {
				while(html.charAt(ht) != ';') {
					ht++;
				}
				ht++;
				pl++;
			} else {
				ht++;
			}
		}
		System.out.println("Difference: " + (ht-pl) );
		return ht-pl;
	}
}

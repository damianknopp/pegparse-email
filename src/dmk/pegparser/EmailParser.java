package dmk.pegparser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

@SuppressWarnings( { "InfiniteRecursion" })
@BuildParseTree
public class EmailParser extends BaseParser<Object> {

	public Rule MessageChain() {
		return Sequence(OneOrMore(Message()), EOI);
	}

	public Rule Message() {
		return Sequence(Headers(), NewLine(), Optional(MessageBody()));
	}

	public Rule Headers(){
		return OneOrMore(Header());
	}

	public Rule Header(){
		return Sequence(HeaderKey(), HeaderDelim(), HeaderValue(), NewLine());
	}
	
	@SuppressNode
	public Rule HeaderDelim(){
		return Ch(':');
	}
	
	@SuppressSubnodes
	public Rule HeaderKey(){
		return FirstOf(String("Message-ID"), String("Subject"), String("Date"), String("From"), String("To"), String("Mime-Version"), String("Content-Type"), String("Content-Transfer-Encoding"), HeaderExt());
	}
	
	public Rule HeaderExt(){
		return Sequence(String("X-"), OneOrMore(ValidHeaderExtChars()));
	}
	
	public Rule ValidHeaderExtChars(){
		return FirstOf(CharRange('A', 'Z'), CharRange('a', 'z'), '-');
	}
	
	@SuppressSubnodes
	public Rule HeaderValue(){
		return AllUpToNewLines();
	}
	
	public Rule AllUpToNewLines(){
		return Sequence(OneOrMore(Sequence(ANY, TestNot(Spaces()))), OneOrMore(Spaces()), OneOrMore(NewLine()));
	}
	
	@SuppressNode
	public Rule NewLine(){
		return Sequence(Optional('\r'), OneOrMore('\n'));
	}
	
	@SuppressNode
	public Rule Spaces(){
		return AnyOf(" \t\r\n");
	}

	public Rule MessageBody(){
		return Sequence(Greetings(), Body());
	}

	@SuppressSubnodes
	public Rule Greetings(){
		return Sequence(Optional(OneOrMore(Spaces())), FirstOf(String("Greetings"), String("Hello"), String("Hi"), String("Dear")), Optional(Ch(',')), AllUpToNewLines());
	}
	
	public Rule Body(){
		return Lines();
	}
	
	@SuppressSubnodes
	public Rule Lines(){
		return OneOrMore(AllUpToNewLines());
	}
}
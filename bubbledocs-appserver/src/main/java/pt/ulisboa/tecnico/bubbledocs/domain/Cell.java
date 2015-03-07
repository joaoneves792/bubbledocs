package pt.ulisboa.tecnico.bubbledocs.domain;

public class Cell extends Cell_Base {

    public Cell(int column, int line, boolean prot) {
        super();
        init(column, line, prot);
      }

    protected void init(int column, int line, boolean prot) {
        set_column(column);
        set_line(line);
        set_protected(prot);
    }

    public Integer getValue(){
        Content content;
        content = getContent();
        if(content == null)
                return null;
        return content.get_value();
    }

    public String toString(){
        Content content;
        content = getContent();
        if(content == null)
                return null;
        return content.toString();
    }
}

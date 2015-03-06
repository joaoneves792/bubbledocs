package pt.ulisboa.tecnico.bubbledocs.domain;

public class Spreadsheet extends Spreadsheet_Base {
    
    public Spreadsheet(String name, String author, Integer id, Integer lines, Integer columns) {
        super();
        init(name, author, id, lines, columns);
    }
    protected void init(String name, String author, Integer id, Integer lines, Integer columns) {
        Cell newCell;

        set_name(name);
        set_author(author);
        set_id(id);
        set_lines(lines);
        set_Columns(columns);
        
        for(int i=1 ; i<=lines; i++){
                for(int j=1 ; j<=columns; j++){
                        newCell = new Cell(j, i, false);
                        addCell(newCell);
                }
        }
    }

}

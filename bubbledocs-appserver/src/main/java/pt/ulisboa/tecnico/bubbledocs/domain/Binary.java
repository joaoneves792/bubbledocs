package pt.ulisboa.tecnico.bubbledocs.domain;

public class Binary extends Binary_Base {
    
    public Binary() {
        super();
    }
    protected void init(String text, Integer int1, Integer int2){
            set_argument1(int1);
            set_argument2(int2);
            super.init(text);
    }

    protected void init(String text, Integer int1, Cell reference2){
            set_argument1(int1);
            addCells(reference2);
            super.init(text);
    }

    protected void init(String text, Cell reference1, Integer int2){
            set_argument2(int2);
            addCells(reference1);
            super.init(text);
    }

    protected void init(String text, Cell reference1, Cell reference2){
            addCells(reference1);
            addCells(reference2);
            super.init(text);
    }

}

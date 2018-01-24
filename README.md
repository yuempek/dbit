# dbit

Sample usage is below


    import com.yuempek.DBit.Column;
    import com.yuempek.DBit.ColumnType;
    import com.yuempek.DBit.DBCollection;
    import com.yuempek.DBit.Database;
    import com.yuempek.DBit.Row;
    import com.yuempek.DBit.Table;
    import com.yuempek.DBit.View;


    public class MainActivity extends Activity implements OnItemClickListener {
        private DBCollection dbc;
        private Database db;
        private Table TABLE_list_items;
        private Table TABLE_lists;
        private View  VIEW_all_list_and_items;
        private View  VIEW_all_relations;

        public void databaseCreation(){

            String view_sql_all_list_and_items = 
                    "select                                    "+"\n"+
                    "   l.listname ,                           "+"\n"+
                    "   ifnull(li.item_name,'') item_name      "+"\n"+
                    "from                                      "+"\n"+
                    "   lists l left outer join                "+"\n"+
                    "   list_items li on (li.list_id = l.id)   "+"\n"+
                    "order by listname, item_name;  ";

            dbc = new DBCollection(this);
            db = dbc.AddDatabase("listit");
            TABLE_list_groups = db.AddTable("list_groups");
            TABLE_list_groups.AddColumn("id", ColumnType.INTEGER, 11).useAsPK().useAsAutoIncremental();

            TABLE_list_items = db.AddTable("list_items");
            TABLE_list_items.AddColumn("id", ColumnType.INTEGER, 11).useAsPK().useAsAutoIncremental();
            TABLE_list_items.AddColumn("item_name", ColumnType.STRING, 45);
            TABLE_list_items.AddColumn("list_id", ColumnType.INTEGER, 11);

            TABLE_lists = db.AddTable("lists");
            TABLE_lists.AddColumn("id", ColumnType.INTEGER, 11).useAsPK().useAsAutoIncremental();
            TABLE_lists.AddColumn("listname", ColumnType.STRING, 45);

            VIEW_all_list_and_items = db.AddView("all_list_and_items", view_sql_all_list_and_items);
            VIEW_all_list_and_items.AddColumn("listname", ColumnType.STRING, 100);
            VIEW_all_list_and_items.AddColumn("itemname", ColumnType.STRING, 100);


            VIEW_all_relations = db.AddView("all_relations", view_sql_all_relations);
            VIEW_all_relations.AddColumn("group_id", ColumnType.INTEGER, 11);
            VIEW_all_relations.AddColumn("listname", ColumnType.STRING, 100);
            VIEW_all_relations.AddColumn("item_name", ColumnType.STRING, 100);

            db.open();
        }

        ....

        databaseCreation();
        db.reCreateDatabase();

        ....

        Column[] columns = new Column[2];
        columns[0] = new Column<String>("item_name", 0, null);;
        columns[0].columnType = ColumnType.STRING;

        columns[1] = new Column<String>("list_id", 0, null);;
        columns[1].columnType = ColumnType.INTEGER;


        // 3 alternative to get rows
        Row[] rows = TABLE_list_items.getData(); // get all data
        Row[] rows = TABLE_list_items.getData("id=3"); //whereConditions
        Row[] rows = TABLE_list_items.getData("id=3", columns); //whereConditions + columns


        ArrayList<String> items = new ArrayList<>();

        for (int i = 0; i < rows.length; i++) 
      {
        items.add((String)rows[i].Column("item_name").value);
        }

        .....


        Row r; 

        r = TABLE_list_items.newRow();

        try {

            r.Column("id").value = 3;
            r.Column("item_name").value = "test";
            r.Column("list_id").value = 2;
            r.insert();

        } catch (Exception e) {
            Log.e(getPackageName(), e.getMessage());
        }


        ......


        import android.database.Cursor;


      public Row[] getSQL(String sql, ArrayList<Column> columns){
            ArrayList<Row> records = new ArrayList<Row>();
            String columnstr = "";

            for(int i = 0; i < columns.size(); i++){
                columnstr += columns.get(i).columnName + ( i + 1 == columns.size() ? "" : ", ");
            }

            Cursor cur =  db.db.rawQuery( "select " + columnstr + " from (" + sql + ") a ", null );
            cur.moveToFirst();

            while(!cur.isAfterLast()){
                Row r = new Row(null);
                Iterator<Column> iter = columns.iterator();

                while(iter.hasNext()){
                    Column column = iter.next().clone();
                    r.put(column.columnName, column);
                }

                String cn;
                Column c;
                for(int i = 0; i < columns.size(); i++){
                    cn = columns.get(i).columnName;
                    try {
                        c = r.Column(cn);
                        c.setValueFromString(cur.getString(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }                 
                }

                records.add(r);
                cur.moveToNext();
            }        

            return records.toArray(new Row[records.size()]);
        }



        ArrayList<Column> columns = new ArrayList<Column>();
        Column newColumn = new Column<String>("listname", 100, null);
        newColumn.columnType = ColumnType.STRING;
        columns.add(newColumn);
        Row[] rows = getSQL(SQL, columns); 

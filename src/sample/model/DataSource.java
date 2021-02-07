package sample.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {
    public static final String DB_NAME = "music";
    public static final String CONNECTION_STRING = "jdbc:sqlserver://DESKTOP-EMSG0FT;databaseName="+DB_NAME+";integratedSecurity=true;";

    public static final String TB_SINGER = "tbSinger";
    public static  String COLUMN_ID_SINGER = "IDSinger";
    public static final String COLUMN_SINGER_NAME = "NameSinger";

    public static final String TB_ALBUM = "tbAlbum";
    public static final String COLUMN_ID_ALBUM = "IDAlbum";
    public static final String COLUMN_ALBUM_NAME = "NameAlbum";
    public static  String COLUMN_ALBUM_SINGERID = "IDSinger";

    public static final String TB_SING = "tbSing";
    public static final String COLUMN_ID_SING = "IDSing";
    public static final String COLUMN_SING_NAME = "NameSing";
    public static  String COLUMN_SING_ALBUMID = "IDAlbum";

    public static final int ASCENDING =1;
    public static final int DESCENDING =2;

    //using singleton
    private DataSource(){}

    private static DataSource instance = new DataSource();

    public static DataSource getInstance(){
        return instance;
    }

    private Connection connect;

    public boolean OpenDB(){
        try {
            System.out.println("Connect to DB.");
            connect = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        }catch(SQLException e){
            System.out.println("Can't connect to DB.");
            return false;
        }
    }

    public void CloseDB(){
        try {
        if(connect != null)
            System.out.println("Close DB.");
                connect.close();
            }catch(SQLException e){
            System.out.println("Can't close DB.");
                e.printStackTrace();
            }
        }

    public ArrayList<Singer> ShowAllSingers(int sorting) {

        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TB_SINGER);

        if(sorting == ASCENDING){
            sb.append(" ORDER BY ");
            sb.append(COLUMN_SINGER_NAME);
            sb.append(" ASC");
            //select * from TbSinger Order By NameSinger asc
        }else{
            sb.append(" ORDER BY ");
            sb.append(COLUMN_SINGER_NAME);
            sb.append(" DESC");
            //select * from TbSinger Order By NameSinger desc
        }

        try (Statement statement = connect.createStatement();
             ResultSet resultSingers = statement.executeQuery(sb.toString())) {
            ArrayList<Singer> AllSingers = new ArrayList<Singer>();
            while (resultSingers.next()) {
                Singer singer = new Singer();
                singer.setIDSinger(resultSingers.getInt(COLUMN_ID_SINGER));
                singer.setName(resultSingers.getString(COLUMN_SINGER_NAME));
                AllSingers.add(singer);
            }
            return AllSingers;

        } catch (SQLException e) {
            System.out.println("Query is wrong." + e.getMessage());
            return null;
        }

    }

    public ArrayList<Album> ShowSingerAllAlbums(int id) {
        String query = "SELECT * FROM TbAlbum "+ "WHERE "+ COLUMN_ALBUM_SINGERID +"= ? ";

        try(PreparedStatement preparedStatement = connect.prepareStatement(query)){

            preparedStatement.setInt(1,id);
            ResultSet resultAlbums = preparedStatement.executeQuery();

            ArrayList<Album> albums = new ArrayList<Album>();

            while(resultAlbums.next()){
                Album album = new Album();
                album.setIDAlbum(resultAlbums.getInt(1));
                album.setName(resultAlbums.getString(2));
                album.setAlbumSingerID(id);

                albums.add(album);
            }

            return albums;

        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Sing> ShowAlbumsSings(int id) {//2
        String query = "SELECT * FROM "+ TB_SING + " WHERE "+ COLUMN_SING_ALBUMID +" = ? ";

        try(PreparedStatement preparedStatement = connect.prepareStatement(query)){
            preparedStatement.setInt(1,id);
            ResultSet resultSings = preparedStatement.executeQuery();

            ArrayList<Sing> sings = new ArrayList<Sing>();

            while(resultSings.next()){
                Sing sing = new Sing();
                sing.setIDSing(resultSings.getInt(1));
                sing.setName(resultSings.getString(2));
                sing.setSingAlbumID(id);

                sings.add(sing);
            }
                return sings;

        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean UpdateSingerName(int id, String newName){
      String query = "UPDATE "+TB_SINGER + " SET "+ COLUMN_SINGER_NAME + " = ? WHERE " + COLUMN_ID_SINGER + " = ? ";

      try(PreparedStatement preparedStatement = connect.prepareStatement(query)){
          preparedStatement.setString(1,newName);
          preparedStatement.setInt(2,id);

          int result =  preparedStatement.executeUpdate();  //bu işlemden kaç satırın etkilendiğini döndürür

          return result == 1;   //1 ise true
      }catch(SQLException e){
          e.printStackTrace();
          return false;
      }

    };

    public Boolean createNewItem(Singer newSinger, Album newAlbum, Sing newSing)    {
        String query1 = "INSERT INTO "+ TB_SINGER +" ("+ COLUMN_SINGER_NAME + ") VALUES ( ? )" ;
        String query2 = "INSERT INTO "+ TB_ALBUM +" ("+ COLUMN_ALBUM_NAME  +" ) VALUES ( ? )" ;
        String query3 = "INSERT INTO "+ TB_SING +" ("+ COLUMN_SING_NAME + " ) VALUES ( ? )" ;

        System.out.println("DataSource: " + newSinger.getName()+" "+newAlbum.getName()+" "+newSing.getName());

       try(PreparedStatement preparedStatement = connect.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1,newSinger.getName());

            int result = preparedStatement.executeUpdate();

            if(result == 0 ){
                throw new SQLException("Creating singer failed, no ID obtained.");
            }

           try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
               if (generatedKeys.next()) {

                   newSinger.setIDSinger((int) generatedKeys.getLong(1));

               }
               else {
                   throw new SQLException("Creating singer failed, no ID obtained.");
               }

           }

        }catch (SQLException e){
            e.printStackTrace();
        }


        try(PreparedStatement preparedStatement = connect.prepareStatement(query2,Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1,newAlbum.getName());
            int result = preparedStatement.executeUpdate();

            newAlbum.setAlbumSingerID(newSinger.getIDSinger());

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newAlbum.setIDAlbum((int) generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating album failed, no ID obtained.");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }


        try(PreparedStatement preparedStatement = connect.prepareStatement(query3,Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1,newSing.getName());
            int result = preparedStatement.executeUpdate();

            newSing.setSingAlbumID(newAlbum.getIDAlbum());


            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newSing.setIDSing((int) generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating sing failed, no ID obtained.");
                }
            }

            //return result==1;//1 ise true
        }catch (SQLException e){
            e.printStackTrace();
            //return false;
        }


        String query4 = "UPDATE " + TB_ALBUM + " SET " + COLUMN_ALBUM_SINGERID + " = ? WHERE "+ COLUMN_ID_ALBUM +" = "+ newAlbum.getIDAlbum();
        String query5 = "UPDATE " + TB_SING + " SET " + COLUMN_SING_ALBUMID + " = ? WHERE "+ COLUMN_ID_SING +" = "+ newSing.getIDSing();

        System.out.println("DATASOURCE IDSinger: " + newSinger.getIDSinger() +
                " NameSinger: "+ newSinger.getName() + " IDAlbum: " + newAlbum.getIDAlbum() +
                " NameAlbum: "+ newAlbum.getName() + " IDAlbumSinger: "+ newAlbum.getAlbumSingerID() +
                " IDSing: "+ newSing.getIDSing() + "NameSing: "+ newSing.getName() + ""+
                " IDSingAlbum: " + newSing.getSingAlbumID());


        try(PreparedStatement preparedStatement = connect.prepareStatement(query4)){
            preparedStatement.setInt(1,newSinger.getIDSinger());

            int result = preparedStatement.executeUpdate();

            //return result==1;//1 ise true
        }catch (SQLException e){
            e.printStackTrace();
            //return false;
        }

        try(PreparedStatement preparedStatement = connect.prepareStatement(query5)){
            preparedStatement.setInt(1,newAlbum.getIDAlbum());

            int result = preparedStatement.executeUpdate();

            return result==1;//1 ise true
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }




    };

}

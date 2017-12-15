package com.lbi.tile.dao;

import com.lbi.map.Tile;
import com.lbi.map.TileSystem;
import com.lbi.tile.model.*;
import com.lbi.util.GeoUtils;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository(value="tmsDao")
public class TMSDao {
    @Resource(name="jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public List<T_TileMap> getTileMapList(){
        List<T_TileMap> list=null;
        try{
            String sql="select * from t_tile_map order by id";
            list=jdbcTemplate.query(
                    sql,
                    new RowMapper<T_TileMap>() {
                        public T_TileMap mapRow(ResultSet rs, int i) throws SQLException {
                            T_TileMap u=new T_TileMap();
                            u.setId(rs.getInt("id"));
                            u.setLayerName(rs.getString("layer_name"));
                            u.setTitle(rs.getString("title"));
                            u.setProfile(rs.getString("profile"));
                            u.setSrs(rs.getString("srs"));
                            u.setHref(rs.getString("href"));
                            u.setUrl(rs.getString("url"));
                            u.setMinX(rs.getDouble("minx"));
                            u.setMinY(rs.getDouble("miny"));
                            u.setMaxX(rs.getDouble("maxx"));
                            u.setMaxY(rs.getDouble("maxy"));
                            u.setOriginX(rs.getDouble("origin_x"));
                            u.setOriginY(rs.getDouble("origin_y"));
                            u.setTileWidth(rs.getInt("tile_width"));
                            u.setTileHeight(rs.getInt("tile_height"));
                            u.setMimeType(rs.getString("mime_type"));
                            u.setExtension(rs.getString("format_extension"));
                            return u;
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    public T_TileMap getTileMapById(String title,String srs,String formatExtension){
        try{
            String sql="select * from t_tile_map where title=? and srs=? and format_extension=?";
            List<T_TileMap> list=jdbcTemplate.query(
                    sql,
                    new Object[]{
                            title,
                            srs,
                            formatExtension
                    },
                    new int[]{
                            Types.VARCHAR,
                            Types.VARCHAR,
                            Types.VARCHAR,
                    },
                    new RowMapper<T_TileMap>() {
                        public T_TileMap mapRow(ResultSet rs, int i) throws SQLException {
                            T_TileMap u=new T_TileMap();
                            u.setId(rs.getInt("id"));
                            u.setLayerName(rs.getString("layer_name"));
                            u.setTitle(rs.getString("title"));
                            u.setProfile(rs.getString("profile"));
                            u.setSrs(rs.getString("srs"));
                            u.setHref(rs.getString("href"));
                            u.setUrl(rs.getString("url"));
                            u.setSType(rs.getInt("s_type"));
                            u.setMinX(rs.getDouble("minx"));
                            u.setMinY(rs.getDouble("miny"));
                            u.setMaxX(rs.getDouble("maxx"));
                            u.setMaxY(rs.getDouble("maxy"));
                            u.setOriginX(rs.getDouble("origin_x"));
                            u.setOriginY(rs.getDouble("origin_y"));
                            u.setTileWidth(rs.getInt("tile_width"));
                            u.setTileHeight(rs.getInt("tile_height"));
                            u.setMimeType(rs.getString("mime_type"));
                            u.setExtension(rs.getString("format_extension"));
                            return u;
                        }
                    });
            if(list.size()>0)return list.get(0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    public List<T_TileSet> getTileSetListByID(int mapId){
        List<T_TileSet> list=null;
        try{
            String sql="select * from t_tile_set where map_id=? order by set_order";
            list=jdbcTemplate.query(
                    sql,
                    new Object[]{mapId},
                    new int[]{Types.INTEGER},
                    new RowMapper<T_TileSet>() {
                        public T_TileSet mapRow(ResultSet rs, int i) throws SQLException {
                            T_TileSet u=new T_TileSet();
                            u.setHref(rs.getString("href"));
                            u.setUnits_per_pixel(rs.getString("units_per_pixel"));
                            u.setOrder(rs.getString("set_order"));
                            return u;
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    public List<Admin_Region> getCityRegionList(Tile tile){
        List<Admin_Region> list=null;
        try{
            Envelope enve= TileSystem.TileXYToBounds(tile);
            Geometry grid= GeoUtils.GEO_FACTORY.toGeometry(enve);
            StringBuilder sb=new StringBuilder();
            String[] fields={"adcode","name","st_astext(geom) as wkt"};
            sb.append("select ").append(StringUtils.join(fields,',')).append(" from s_ods_city_simplify");
            sb.append(" where st_intersects(st_geomfromtext(?,4326),geom)");
            list=jdbcTemplate.query(
                    sb.toString(),
                    new Object[]{grid.toText()},
                    new int[]{Types.VARCHAR},
                    new RowMapper<Admin_Region>() {
                        public Admin_Region mapRow(ResultSet rs, int i) throws SQLException {
                            Admin_Region u=new Admin_Region();
                            u.setCode(rs.getString("adcode"));
                            u.setName(rs.getString("name"));
                            u.setWkt(rs.getString("wkt"));
                            return u;
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    public List<Admin_Region> getCityRegionList2(Tile tile){
        List<Admin_Region> list=null;
        try{
            Envelope enve= TileSystem.TileXYToBounds(tile);
            Geometry grid=GeoUtils.GEO_FACTORY.toGeometry(enve);
            StringBuilder sb=new StringBuilder();
            String[] fields={"adcode","name","st_astext(ST_Transform(geom,900913)) as wkt"};
            sb.append("select ").append(StringUtils.join(fields,',')).append(" from s_ods_city_simplify");
            sb.append(" where st_intersects(st_geomfromtext(?,4326),geom)");
            list=jdbcTemplate.query(
                    sb.toString(),
                    new Object[]{grid.toText()},
                    new int[]{Types.VARCHAR},
                    new RowMapper<Admin_Region>() {
                        public Admin_Region mapRow(ResultSet rs, int i) throws SQLException {
                            Admin_Region u=new Admin_Region();
                            u.setCode(rs.getString("adcode"));
                            u.setName(rs.getString("name"));
                            u.setWkt(rs.getString("wkt"));
                            return u;
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    public List<Map<String,Object>> getPoiList(Tile tile){
        List<Map<String,Object>> list=null;
        try{
            Envelope enve= TileSystem.TileXYToBounds(tile);
            Geometry grid=GeoUtils.GEO_FACTORY.toGeometry(enve);
            StringBuilder sb=new StringBuilder();
            String[] fields={"id","chi_name","st_astext(geom) as wkt"};
            sb.append("select ").append(StringUtils.join(fields,',')).append(" from navinfo_poi_17q2");
            sb.append(" where st_intersects(st_geomfromtext(?,4326),geom)");
            list=jdbcTemplate.query(
                    sb.toString(),
                    new Object[]{grid.toText()},
                    new int[]{Types.VARCHAR},
                    new RowMapper<Map<String,Object>>() {
                        public Map<String,Object> mapRow(ResultSet rs, int i) throws SQLException {
                            Map<String,Object> u=new HashMap<String,Object>();
                            u.put("id",rs.getLong("id"));
                            u.put("name",rs.getString("chi_name"));
                            u.put("wkt",rs.getString("wkt"));
                            return u;
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
}

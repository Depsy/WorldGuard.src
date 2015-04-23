/*      */ package com.sk89q.worldguard.protection.databases;
/*      */ 
/*      */ import com.sk89q.worldedit.BlockVector;
/*      */ import com.sk89q.worldedit.BlockVector2D;
/*      */ import com.sk89q.worldedit.Vector;
/*      */ import com.sk89q.worldguard.bukkit.ConfigurationManager;
/*      */ import com.sk89q.worldguard.domains.DefaultDomain;
/*      */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*      */ import com.sk89q.worldguard.protection.flags.Flag;
/*      */ import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DriverManager;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import org.yaml.snakeyaml.DumperOptions;
/*      */ import org.yaml.snakeyaml.DumperOptions.FlowStyle;
/*      */ import org.yaml.snakeyaml.Yaml;
/*      */ import org.yaml.snakeyaml.constructor.SafeConstructor;
/*      */ import org.yaml.snakeyaml.error.YAMLException;
/*      */ import org.yaml.snakeyaml.representer.Representer;
/*      */ 
/*      */ public class MySQLDatabase extends AbstractProtectionDatabase
/*      */ {
/*      */   private final Logger logger;
/*      */   private Yaml yaml;
/*      */   private Map<String, ProtectedRegion> regions;
/*      */   private Map<String, ProtectedRegion> cuboidRegions;
/*      */   private Map<String, ProtectedRegion> poly2dRegions;
/*      */   private Map<String, ProtectedRegion> globalRegions;
/*      */   private Map<ProtectedRegion, String> parentSets;
/*      */   private final ConfigurationManager config;
/*      */   private Connection conn;
/*   62 */   private int worldDbId = -1;
/*      */ 
/*      */   public MySQLDatabase(ConfigurationManager config, String world, Logger logger) throws ProtectionDatabaseException {
/*   65 */     this.config = config;
/*   66 */     String world1 = world;
/*   67 */     this.logger = logger;
/*      */     try
/*      */     {
/*   70 */       connect();
/*      */       try
/*      */       {
/*   74 */         PreparedStatement verTest = this.conn.prepareStatement("SELECT `world_id` FROM `region_cuboid` LIMIT 0,1;");
/*      */ 
/*   77 */         verTest.execute();
/*      */       } catch (SQLException ex) {
/*   79 */         throw new InvalidTableFormatException("region_storage_update_20110325.sql");
/*      */       }
/*      */ 
/*   84 */       PreparedStatement worldStmt = this.conn.prepareStatement("SELECT `id` FROM `world` WHERE `name` = ? LIMIT 0,1");
/*      */ 
/*   90 */       worldStmt.setString(1, world1);
/*   91 */       ResultSet worldResult = worldStmt.executeQuery();
/*      */ 
/*   93 */       if (worldResult.first()) {
/*   94 */         this.worldDbId = worldResult.getInt("id");
/*      */       } else {
/*   96 */         PreparedStatement insertWorldStatement = this.conn.prepareStatement("INSERT INTO `world` (`id`, `name`) VALUES (null, ?)", 1);
/*      */ 
/*  103 */         insertWorldStatement.setString(1, world);
/*  104 */         insertWorldStatement.execute();
/*  105 */         ResultSet generatedKeys = insertWorldStatement.getGeneratedKeys();
/*  106 */         if (generatedKeys.first())
/*  107 */           this.worldDbId = generatedKeys.getInt(1);
/*      */       }
/*      */     }
/*      */     catch (SQLException ex) {
/*  111 */       logger.log(Level.SEVERE, ex.getMessage(), ex);
/*      */ 
/*  114 */       return;
/*      */     }
/*      */ 
/*  117 */     if (this.worldDbId <= 0) {
/*  118 */       logger.log(Level.SEVERE, "Could not find or create the world");
/*      */ 
/*  121 */       return;
/*      */     }
/*      */ 
/*  124 */     DumperOptions options = new DumperOptions();
/*  125 */     options.setIndent(2);
/*  126 */     options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
/*  127 */     Representer representer = new Representer();
/*  128 */     representer.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
/*      */ 
/*  131 */     this.yaml = new Yaml(new SafeConstructor(), new Representer(), options);
/*      */   }
/*      */ 
/*      */   private void connect() throws SQLException {
/*  135 */     if (this.conn != null) {
/*      */       try
/*      */       {
/*  138 */         this.conn.prepareStatement("SELECT 1;").execute();
/*      */       }
/*      */       catch (SQLException ex)
/*      */       {
/*  144 */         if ("08S01".equals(ex.getSQLState())) {
/*  145 */           this.conn.close();
/*      */         }
/*      */       }
/*      */     }
/*  149 */     if ((this.conn == null) || (this.conn.isClosed()))
/*  150 */       this.conn = DriverManager.getConnection(this.config.sqlDsn, this.config.sqlUsername, this.config.sqlPassword);
/*      */   }
/*      */ 
/*      */   private void loadFlags(ProtectedRegion region)
/*      */   {
/*      */     try
/*      */     {
/*  157 */       PreparedStatement flagsStatement = this.conn.prepareStatement("SELECT `region_flag`.`flag`, `region_flag`.`value` FROM `region_flag` WHERE `region_flag`.`region_id` = ? AND `region_flag`.`world_id` = " + this.worldDbId);
/*      */ 
/*  166 */       flagsStatement.setString(1, region.getId().toLowerCase());
/*  167 */       ResultSet flagsResultSet = flagsStatement.executeQuery();
/*      */ 
/*  169 */       Map regionFlags = new HashMap();
/*  170 */       while (flagsResultSet.next()) {
/*  171 */         regionFlags.put(flagsResultSet.getString("flag"), sqlUnmarshal(flagsResultSet.getString("value")));
/*      */       }
/*      */ 
/*  178 */       for (Flag flag : DefaultFlag.getFlags()) {
/*  179 */         Object o = regionFlags.get(flag.getName());
/*  180 */         if (o != null)
/*  181 */           setFlag(region, flag, o);
/*      */       }
/*      */     }
/*      */     catch (SQLException ex) {
/*  185 */       this.logger.warning("Unable to load flags for region " + region.getId().toLowerCase() + ": " + ex.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   private <T> void setFlag(ProtectedRegion region, Flag<T> flag, Object rawValue)
/*      */   {
/*  193 */     Object val = flag.unmarshal(rawValue);
/*  194 */     if (val == null) {
/*  195 */       this.logger.warning("Failed to parse flag '" + flag.getName() + "' with value '" + rawValue.toString() + "'");
/*      */ 
/*  197 */       return;
/*      */     }
/*  199 */     region.setFlag(flag, val);
/*      */   }
/*      */ 
/*      */   private void loadOwnersAndMembers(ProtectedRegion region) {
/*  203 */     DefaultDomain owners = new DefaultDomain();
/*  204 */     DefaultDomain members = new DefaultDomain();
/*      */     try
/*      */     {
/*  207 */       PreparedStatement usersStatement = this.conn.prepareStatement("SELECT `user`.`name`, `region_players`.`owner` FROM `region_players` LEFT JOIN `user` ON ( `region_players`.`user_id` = `user`.`id`) WHERE `region_players`.`region_id` = ? AND `region_players`.`world_id` = " + this.worldDbId);
/*      */ 
/*  219 */       usersStatement.setString(1, region.getId().toLowerCase());
/*  220 */       ResultSet userSet = usersStatement.executeQuery();
/*  221 */       while (userSet.next())
/*  222 */         if (userSet.getBoolean("owner"))
/*  223 */           owners.addPlayer(userSet.getString("name"));
/*      */         else
/*  225 */           members.addPlayer(userSet.getString("name"));
/*      */     }
/*      */     catch (SQLException ex)
/*      */     {
/*  229 */       this.logger.warning("Unable to load users for region " + region.getId().toLowerCase() + ": " + ex.getMessage());
/*      */     }
/*      */     try
/*      */     {
/*  233 */       PreparedStatement groupsStatement = this.conn.prepareStatement("SELECT `group`.`name`, `region_groups`.`owner` FROM `region_groups` LEFT JOIN `group` ON ( `region_groups`.`group_id` = `group`.`id`) WHERE `region_groups`.`region_id` = ? AND `region_groups`.`world_id` = " + this.worldDbId);
/*      */ 
/*  245 */       groupsStatement.setString(1, region.getId().toLowerCase());
/*  246 */       ResultSet groupSet = groupsStatement.executeQuery();
/*  247 */       while (groupSet.next())
/*  248 */         if (groupSet.getBoolean("owner"))
/*  249 */           owners.addGroup(groupSet.getString("name"));
/*      */         else
/*  251 */           members.addGroup(groupSet.getString("name"));
/*      */     }
/*      */     catch (SQLException ex)
/*      */     {
/*  255 */       this.logger.warning("Unable to load groups for region " + region.getId().toLowerCase() + ": " + ex.getMessage());
/*      */     }
/*      */ 
/*  258 */     region.setOwners(owners);
/*  259 */     region.setMembers(members);
/*      */   }
/*      */ 
/*      */   private void loadGlobal() {
/*  263 */     Map regions = new HashMap();
/*      */     Throwable t;
/*      */     try {
/*  267 */       PreparedStatement globalRegionStatement = this.conn.prepareStatement("SELECT `region`.`id`, `region`.`priority`, `parent`.`id` AS `parent` FROM `region` LEFT JOIN `region` AS `parent` ON (`region`.`parent` = `parent`.`id` AND `region`.`world_id` = `parent`.`world_id`) WHERE `region`.`type` = 'global' AND `region`.`world_id` = ? ");
/*      */ 
/*  280 */       globalRegionStatement.setInt(1, this.worldDbId);
/*  281 */       ResultSet globalResultSet = globalRegionStatement.executeQuery();
/*      */ 
/*  283 */       while (globalResultSet.next()) {
/*  284 */         ProtectedRegion region = new GlobalProtectedRegion(globalResultSet.getString("id"));
/*      */ 
/*  286 */         region.setPriority(globalResultSet.getInt("priority"));
/*      */ 
/*  288 */         loadFlags(region);
/*  289 */         loadOwnersAndMembers(region);
/*      */ 
/*  291 */         regions.put(globalResultSet.getString("id"), region);
/*      */ 
/*  293 */         String parentId = globalResultSet.getString("parent");
/*  294 */         if (parentId != null)
/*  295 */           this.parentSets.put(region, parentId);
/*      */       }
/*      */     }
/*      */     catch (SQLException ex) {
/*  299 */       ex.printStackTrace();
/*  300 */       this.logger.warning("Unable to load regions from sql database: " + ex.getMessage());
/*  301 */       t = ex.getCause();
/*  302 */     }while (t != null) {
/*  303 */       this.logger.warning("\t\tCause: " + t.getMessage());
/*  304 */       t = t.getCause();
/*      */     }
/*      */ 
/*  308 */     this.globalRegions = regions;
/*      */   }
/*      */ 
/*      */   private void loadCuboid() {
/*  312 */     Map regions = new HashMap();
/*      */     Throwable t;
/*      */     try {
/*  316 */       PreparedStatement cuboidRegionStatement = this.conn.prepareStatement("SELECT `region_cuboid`.`min_z`, `region_cuboid`.`min_y`, `region_cuboid`.`min_x`, `region_cuboid`.`max_z`, `region_cuboid`.`max_y`, `region_cuboid`.`max_x`, `region`.`id`, `region`.`priority`, `parent`.`id` AS `parent` FROM `region_cuboid` LEFT JOIN `region` ON (`region_cuboid`.`region_id` = `region`.`id` AND `region_cuboid`.`world_id` = `region`.`world_id`) LEFT JOIN `region` AS `parent` ON (`region`.`parent` = `parent`.`id` AND `region`.`world_id` = `parent`.`world_id`) WHERE `region`.`world_id` = ? ");
/*      */ 
/*  337 */       cuboidRegionStatement.setInt(1, this.worldDbId);
/*  338 */       ResultSet cuboidResultSet = cuboidRegionStatement.executeQuery();
/*      */ 
/*  340 */       while (cuboidResultSet.next()) {
/*  341 */         Vector pt1 = new Vector(cuboidResultSet.getInt("min_x"), cuboidResultSet.getInt("min_y"), cuboidResultSet.getInt("min_z"));
/*      */ 
/*  346 */         Vector pt2 = new Vector(cuboidResultSet.getInt("max_x"), cuboidResultSet.getInt("max_y"), cuboidResultSet.getInt("max_z"));
/*      */ 
/*  352 */         BlockVector min = Vector.getMinimum(pt1, pt2).toBlockVector();
/*  353 */         BlockVector max = Vector.getMaximum(pt1, pt2).toBlockVector();
/*  354 */         ProtectedRegion region = new ProtectedCuboidRegion(cuboidResultSet.getString("id"), min, max);
/*      */ 
/*  360 */         region.setPriority(cuboidResultSet.getInt("priority"));
/*      */ 
/*  362 */         loadFlags(region);
/*  363 */         loadOwnersAndMembers(region);
/*      */ 
/*  365 */         regions.put(cuboidResultSet.getString("id"), region);
/*      */ 
/*  367 */         String parentId = cuboidResultSet.getString("parent");
/*  368 */         if (parentId != null)
/*  369 */           this.parentSets.put(region, parentId);
/*      */       }
/*      */     }
/*      */     catch (SQLException ex)
/*      */     {
/*  374 */       ex.printStackTrace();
/*  375 */       this.logger.warning("Unable to load regions from sql database: " + ex.getMessage());
/*  376 */       t = ex.getCause();
/*  377 */     }while (t != null) {
/*  378 */       this.logger.warning("\t\tCause: " + t.getMessage());
/*  379 */       t = t.getCause();
/*      */     }
/*      */ 
/*  383 */     this.cuboidRegions = regions;
/*      */   }
/*      */ 
/*      */   private void loadPoly2d() {
/*  387 */     Map regions = new HashMap();
/*      */     Throwable t;
/*      */     try {
/*  391 */       PreparedStatement poly2dRegionStatement = this.conn.prepareStatement("SELECT `region_poly2d`.`min_y`, `region_poly2d`.`max_y`, `region`.`id`, `region`.`priority`, `parent`.`id` AS `parent` FROM `region_poly2d` LEFT JOIN `region` ON (`region_poly2d`.`region_id` = `region`.`id` AND `region_poly2d`.`world_id` = `region`.`world_id`) LEFT JOIN `region` AS `parent` ON (`region`.`parent` = `parent`.`id` AND `region`.`world_id` = `parent`.`world_id`) WHERE `region`.`world_id` = ? ");
/*      */ 
/*  408 */       poly2dRegionStatement.setInt(1, this.worldDbId);
/*  409 */       ResultSet poly2dResultSet = poly2dRegionStatement.executeQuery();
/*      */ 
/*  411 */       PreparedStatement poly2dVectorStatement = this.conn.prepareStatement("SELECT `region_poly2d_point`.`x`, `region_poly2d_point`.`z` FROM `region_poly2d_point` WHERE `region_poly2d_point`.`region_id` = ? AND `region_poly2d_point`.`world_id` = " + this.worldDbId);
/*      */ 
/*  420 */       while (poly2dResultSet.next()) {
/*  421 */         String id = poly2dResultSet.getString("id");
/*      */ 
/*  423 */         Integer minY = Integer.valueOf(poly2dResultSet.getInt("min_y"));
/*  424 */         Integer maxY = Integer.valueOf(poly2dResultSet.getInt("max_y"));
/*  425 */         List points = new ArrayList();
/*      */ 
/*  427 */         poly2dVectorStatement.setString(1, id);
/*  428 */         ResultSet poly2dVectorResultSet = poly2dVectorStatement.executeQuery();
/*      */ 
/*  430 */         while (poly2dVectorResultSet.next()) {
/*  431 */           points.add(new BlockVector2D(poly2dVectorResultSet.getInt("x"), poly2dVectorResultSet.getInt("z")));
/*      */         }
/*      */ 
/*  436 */         ProtectedRegion region = new ProtectedPolygonalRegion(id, points, minY.intValue(), maxY.intValue());
/*      */ 
/*  438 */         region.setPriority(poly2dResultSet.getInt("priority"));
/*      */ 
/*  440 */         loadFlags(region);
/*  441 */         loadOwnersAndMembers(region);
/*      */ 
/*  443 */         regions.put(poly2dResultSet.getString("id"), region);
/*      */ 
/*  445 */         String parentId = poly2dResultSet.getString("parent");
/*  446 */         if (parentId != null)
/*  447 */           this.parentSets.put(region, parentId);
/*      */       }
/*      */     }
/*      */     catch (SQLException ex) {
/*  451 */       ex.printStackTrace();
/*  452 */       this.logger.warning("Unable to load regions from sql database: " + ex.getMessage());
/*  453 */       t = ex.getCause();
/*  454 */     }while (t != null) {
/*  455 */       this.logger.warning("\t\tCause: " + t.getMessage());
/*  456 */       t = t.getCause();
/*      */     }
/*      */ 
/*  460 */     this.poly2dRegions = regions;
/*      */   }
/*      */ 
/*      */   public void load() throws ProtectionDatabaseException
/*      */   {
/*      */     try {
/*  466 */       connect();
/*      */     } catch (SQLException ex) {
/*  468 */       throw new ProtectionDatabaseException(ex);
/*      */     }
/*      */ 
/*  471 */     this.parentSets = new HashMap();
/*      */ 
/*  475 */     loadCuboid();
/*  476 */     Map regions = this.cuboidRegions;
/*  477 */     this.cuboidRegions = null;
/*      */ 
/*  479 */     loadPoly2d();
/*  480 */     regions.putAll(this.poly2dRegions);
/*  481 */     this.poly2dRegions = null;
/*      */ 
/*  483 */     loadGlobal();
/*  484 */     regions.putAll(this.globalRegions);
/*  485 */     this.globalRegions = null;
/*      */ 
/*  488 */     for (Map.Entry entry : this.parentSets.entrySet()) {
/*  489 */       ProtectedRegion parent = (ProtectedRegion)regions.get(entry.getValue());
/*  490 */       if (parent != null) {
/*      */         try {
/*  492 */           ((ProtectedRegion)entry.getKey()).setParent(parent);
/*      */         } catch (ProtectedRegion.CircularInheritanceException e) {
/*  494 */           this.logger.warning("Circular inheritance detect with '" + (String)entry.getValue() + "' detected as a parent");
/*      */         }
/*      */       }
/*      */       else {
/*  498 */         this.logger.warning("Unknown region parent: " + (String)entry.getValue());
/*      */       }
/*      */     }
/*      */ 
/*  502 */     this.regions = regions;
/*      */   }
/*      */ 
/*      */   private Map<String, Integer> getUserIds(String[] usernames)
/*      */   {
/*  511 */     Map users = new HashMap();
/*      */ 
/*  513 */     if (usernames.length < 1) return users; Throwable t;
/*      */     try
/*      */     {
/*  516 */       PreparedStatement findUsersStatement = this.conn.prepareStatement(String.format("SELECT `user`.`id`, `user`.`name` FROM `user` WHERE `name` IN (%s)", new Object[] { RegionDBUtil.preparePlaceHolders(usernames.length) }));
/*      */ 
/*  527 */       RegionDBUtil.setValues(findUsersStatement, usernames);
/*      */ 
/*  529 */       ResultSet findUsersResults = findUsersStatement.executeQuery();
/*      */ 
/*  531 */       while (findUsersResults.next()) {
/*  532 */         users.put(findUsersResults.getString("name"), Integer.valueOf(findUsersResults.getInt("id")));
/*      */       }
/*      */ 
/*  535 */       PreparedStatement insertUserStatement = this.conn.prepareStatement("INSERT INTO `user` ( `id`, `name`) VALUES (null, ?)", 1);
/*      */ 
/*  544 */       for (String username : usernames)
/*  545 */         if (!users.containsKey(username)) {
/*  546 */           insertUserStatement.setString(1, username);
/*  547 */           insertUserStatement.execute();
/*  548 */           ResultSet generatedKeys = insertUserStatement.getGeneratedKeys();
/*  549 */           if (generatedKeys.first())
/*  550 */             users.put(username, Integer.valueOf(generatedKeys.getInt(1)));
/*      */           else
/*  552 */             this.logger.warning("Could not get the database id for user " + username);
/*      */         }
/*      */     }
/*      */     catch (SQLException ex)
/*      */     {
/*  557 */       ex.printStackTrace();
/*  558 */       this.logger.warning("Could not get the database id for the users " + usernames.toString() + "\n\t" + ex.getMessage());
/*  559 */       t = ex.getCause();
/*  560 */     }while (t != null) {
/*  561 */       this.logger.warning(t.getMessage());
/*  562 */       t = t.getCause();
/*      */     }
/*      */ 
/*  566 */     return users;
/*      */   }
/*      */ 
/*      */   private Map<String, Integer> getGroupIds(String[] groupnames)
/*      */   {
/*  575 */     Map groups = new HashMap();
/*      */ 
/*  577 */     if (groupnames.length < 1) return groups;
/*      */     try
/*      */     {
/*  580 */       PreparedStatement findGroupsStatement = this.conn.prepareStatement(String.format("SELECT `group`.`id`, `group`.`name` FROM `group` WHERE `name` IN (%s)", new Object[] { RegionDBUtil.preparePlaceHolders(groupnames.length) }));
/*      */ 
/*  591 */       RegionDBUtil.setValues(findGroupsStatement, groupnames);
/*      */ 
/*  593 */       ResultSet findGroupsResults = findGroupsStatement.executeQuery();
/*      */ 
/*  595 */       while (findGroupsResults.next()) {
/*  596 */         groups.put(findGroupsResults.getString("name"), Integer.valueOf(findGroupsResults.getInt("id")));
/*      */       }
/*      */ 
/*  599 */       PreparedStatement insertGroupStatement = this.conn.prepareStatement("INSERT INTO `group` ( `id`, `name`) VALUES (null, ?)", 1);
/*      */ 
/*  608 */       for (String groupname : groupnames)
/*  609 */         if (!groups.containsKey(groupname)) {
/*  610 */           insertGroupStatement.setString(1, groupname);
/*  611 */           insertGroupStatement.execute();
/*  612 */           ResultSet generatedKeys = insertGroupStatement.getGeneratedKeys();
/*  613 */           if (generatedKeys.first())
/*  614 */             groups.put(groupname, Integer.valueOf(generatedKeys.getInt(1)));
/*      */           else
/*  616 */             this.logger.warning("Could not get the database id for user " + groupname);
/*      */         }
/*      */     }
/*      */     catch (SQLException ex)
/*      */     {
/*  621 */       this.logger.warning("Could not get the database id for the groups " + groupnames.toString() + ex.getMessage());
/*      */     }
/*      */ 
/*  624 */     return groups;
/*      */   }
/*      */ 
/*      */   public void save()
/*      */     throws ProtectionDatabaseException
/*      */   {
/*      */     try
/*      */     {
/*  645 */       connect();
/*      */     } catch (SQLException ex) {
/*  647 */       throw new ProtectionDatabaseException(ex);
/*      */     }
/*      */ 
/*  650 */     List regionsInDatabase = new ArrayList();
/*      */     try
/*      */     {
/*  653 */       PreparedStatement getAllRegionsStatement = this.conn.prepareStatement("SELECT `region`.`id` FROM `region` WHERE `world_id` = ? ");
/*      */ 
/*  659 */       getAllRegionsStatement.setInt(1, this.worldDbId);
/*  660 */       ResultSet getAllRegionsResult = getAllRegionsStatement.executeQuery();
/*      */ 
/*  662 */       while (getAllRegionsResult.next())
/*  663 */         regionsInDatabase.add(getAllRegionsResult.getString("id"));
/*      */     }
/*      */     catch (SQLException ex) {
/*  666 */       this.logger.warning("Could not get region list for save comparison: " + ex.getMessage());
/*      */     }
/*      */ 
/*  669 */     for (Map.Entry entry : this.regions.entrySet()) {
/*  670 */       String name = (String)entry.getKey();
/*  671 */       ProtectedRegion region = (ProtectedRegion)entry.getValue();
/*      */       try
/*      */       {
/*  674 */         if (regionsInDatabase.contains(name)) {
/*  675 */           regionsInDatabase.remove(name);
/*      */ 
/*  677 */           if ((region instanceof ProtectedCuboidRegion))
/*  678 */             updateRegionCuboid((ProtectedCuboidRegion)region);
/*  679 */           else if ((region instanceof ProtectedPolygonalRegion))
/*  680 */             updateRegionPoly2D((ProtectedPolygonalRegion)region);
/*  681 */           else if ((region instanceof GlobalProtectedRegion))
/*  682 */             updateRegionGlobal((GlobalProtectedRegion)region);
/*      */           else {
/*  684 */             updateRegion(region, region.getClass().getCanonicalName());
/*      */           }
/*      */         }
/*  687 */         else if ((region instanceof ProtectedCuboidRegion)) {
/*  688 */           insertRegionCuboid((ProtectedCuboidRegion)region);
/*  689 */         } else if ((region instanceof ProtectedPolygonalRegion)) {
/*  690 */           insertRegionPoly2D((ProtectedPolygonalRegion)region);
/*  691 */         } else if ((region instanceof GlobalProtectedRegion)) {
/*  692 */           insertRegionGlobal((GlobalProtectedRegion)region);
/*      */         } else {
/*  694 */           insertRegion(region, region.getClass().getCanonicalName());
/*      */         }
/*      */       }
/*      */       catch (SQLException ex) {
/*  698 */         this.logger.warning("Could not save region " + region.getId().toLowerCase() + ": " + ex.getMessage());
/*  699 */         throw new ProtectionDatabaseException(ex);
/*      */       }
/*      */     }
/*      */ 
/*  703 */     for (Map.Entry entry : this.regions.entrySet()) {
/*      */       try {
/*  705 */         if (((ProtectedRegion)entry.getValue()).getParent() != null)
/*      */         {
/*  707 */           PreparedStatement setParentStatement = this.conn.prepareStatement("UPDATE `region` SET `parent` = ? WHERE `id` = ? AND `world_id` = " + this.worldDbId);
/*      */ 
/*  713 */           setParentStatement.setString(1, ((ProtectedRegion)entry.getValue()).getParent().getId().toLowerCase());
/*  714 */           setParentStatement.setString(2, ((ProtectedRegion)entry.getValue()).getId().toLowerCase());
/*      */ 
/*  716 */           setParentStatement.execute();
/*      */         }
/*      */       } catch (SQLException ex) { this.logger.warning("Could not save region parents " + ((ProtectedRegion)entry.getValue()).getId().toLowerCase() + ": " + ex.getMessage());
/*  719 */         throw new ProtectionDatabaseException(ex);
/*      */       }
/*      */     }
/*      */ 
/*  723 */     for (String name : regionsInDatabase)
/*      */       try {
/*  725 */         PreparedStatement removeRegion = this.conn.prepareStatement("DELETE FROM `region` WHERE `id` = ? ");
/*      */ 
/*  729 */         removeRegion.setString(1, name);
/*  730 */         removeRegion.execute();
/*      */       } catch (SQLException ex) {
/*  732 */         this.logger.warning("Could not remove region from database " + name + ": " + ex.getMessage());
/*      */       }
/*      */   }
/*      */ 
/*      */   private void updateFlags(ProtectedRegion region)
/*      */     throws SQLException
/*      */   {
/*  739 */     PreparedStatement clearCurrentFlagStatement = this.conn.prepareStatement("DELETE FROM `region_flag` WHERE `region_id` = ? AND `world_id` = " + this.worldDbId);
/*      */ 
/*  745 */     clearCurrentFlagStatement.setString(1, region.getId().toLowerCase());
/*  746 */     clearCurrentFlagStatement.execute();
/*      */ 
/*  748 */     for (Map.Entry entry : region.getFlags().entrySet())
/*  749 */       if (entry.getValue() != null)
/*      */       {
/*  751 */         Object flag = sqlMarshal(marshalFlag((Flag)entry.getKey(), entry.getValue()));
/*      */ 
/*  753 */         PreparedStatement insertFlagStatement = this.conn.prepareStatement("INSERT INTO `region_flag` ( `id`, `region_id`, `world_id`, `flag`, `value` ) VALUES (null, ?, " + this.worldDbId + ", ?, ?)");
/*      */ 
/*  763 */         insertFlagStatement.setString(1, region.getId().toLowerCase());
/*  764 */         insertFlagStatement.setString(2, ((Flag)entry.getKey()).getName());
/*  765 */         insertFlagStatement.setObject(3, flag);
/*      */ 
/*  767 */         insertFlagStatement.execute();
/*      */       }
/*      */   }
/*      */ 
/*      */   private void updatePlayerAndGroups(ProtectedRegion region, Boolean owners)
/*      */     throws SQLException
/*      */   {
/*      */     DefaultDomain domain;
/*      */     DefaultDomain domain;
/*  774 */     if (owners.booleanValue())
/*  775 */       domain = region.getOwners();
/*      */     else {
/*  777 */       domain = region.getMembers();
/*      */     }
/*      */ 
/*  780 */     PreparedStatement deleteUsersForRegion = this.conn.prepareStatement("DELETE FROM `region_players` WHERE `region_id` = ? AND `world_id` = " + this.worldDbId + " " + "AND `owner` = ?");
/*      */ 
/*  787 */     deleteUsersForRegion.setString(1, region.getId().toLowerCase());
/*  788 */     deleteUsersForRegion.setBoolean(2, owners.booleanValue());
/*  789 */     deleteUsersForRegion.execute();
/*      */ 
/*  791 */     PreparedStatement insertUsersForRegion = this.conn.prepareStatement("INSERT INTO `region_players` (`region_id`, `world_id`, `user_id`, `owner`) VALUES (?, " + this.worldDbId + ",  ?, ?)");
/*      */ 
/*  797 */     Set var = domain.getPlayers();
/*      */ 
/*  799 */     for (Integer player : getUserIds((String[])var.toArray(new String[var.size()])).values()) {
/*  800 */       insertUsersForRegion.setString(1, region.getId().toLowerCase());
/*  801 */       insertUsersForRegion.setInt(2, player.intValue());
/*  802 */       insertUsersForRegion.setBoolean(3, owners.booleanValue());
/*      */ 
/*  804 */       insertUsersForRegion.execute();
/*      */     }
/*      */ 
/*  807 */     PreparedStatement deleteGroupsForRegion = this.conn.prepareStatement("DELETE FROM `region_groups` WHERE `region_id` = ? AND `world_id` = " + this.worldDbId + " " + "AND `owner` = ?");
/*      */ 
/*  814 */     deleteGroupsForRegion.setString(1, region.getId().toLowerCase());
/*  815 */     deleteGroupsForRegion.setBoolean(2, owners.booleanValue());
/*  816 */     deleteGroupsForRegion.execute();
/*      */ 
/*  818 */     PreparedStatement insertGroupsForRegion = this.conn.prepareStatement("INSERT INTO `region_groups` (`region_id`, `world_id`, `group_id`, `owner`) VALUES (?, " + this.worldDbId + ",  ?, ?)");
/*      */ 
/*  824 */     Set groupVar = domain.getGroups();
/*  825 */     for (Integer group : getGroupIds((String[])groupVar.toArray(new String[groupVar.size()])).values()) {
/*  826 */       insertGroupsForRegion.setString(1, region.getId().toLowerCase());
/*  827 */       insertGroupsForRegion.setInt(2, group.intValue());
/*  828 */       insertGroupsForRegion.setBoolean(3, owners.booleanValue());
/*      */ 
/*  830 */       insertGroupsForRegion.execute();
/*      */     }
/*      */   }
/*      */ 
/*      */   private <V> Object marshalFlag(Flag<V> flag, Object val)
/*      */   {
/*  836 */     return flag.marshal(val);
/*      */   }
/*      */ 
/*      */   private void insertRegion(ProtectedRegion region, String type) throws SQLException {
/*  840 */     PreparedStatement insertRegionStatement = this.conn.prepareStatement("INSERT INTO `region` (`id`, `world_id`, `type`, `priority`, `parent` ) VALUES (?, ?, ?, ?, null)");
/*      */ 
/*  850 */     insertRegionStatement.setString(1, region.getId().toLowerCase());
/*  851 */     insertRegionStatement.setInt(2, this.worldDbId);
/*  852 */     insertRegionStatement.setString(3, type);
/*  853 */     insertRegionStatement.setInt(4, region.getPriority());
/*      */ 
/*  855 */     insertRegionStatement.execute();
/*      */ 
/*  857 */     updateFlags(region);
/*      */ 
/*  859 */     updatePlayerAndGroups(region, Boolean.valueOf(false));
/*  860 */     updatePlayerAndGroups(region, Boolean.valueOf(true));
/*      */   }
/*      */ 
/*      */   private void insertRegionCuboid(ProtectedCuboidRegion region) throws SQLException {
/*  864 */     insertRegion(region, "cuboid");
/*      */ 
/*  866 */     PreparedStatement insertCuboidRegionStatement = this.conn.prepareStatement("INSERT INTO `region_cuboid` (`region_id`, `world_id`, `min_z`, `min_y`, `min_x`, `max_z`, `max_y`, `max_x` ) VALUES (?, " + this.worldDbId + ", ?, ?, ?, ?, ?, ?)");
/*      */ 
/*  879 */     BlockVector min = region.getMinimumPoint();
/*  880 */     BlockVector max = region.getMaximumPoint();
/*      */ 
/*  882 */     insertCuboidRegionStatement.setString(1, region.getId().toLowerCase());
/*  883 */     insertCuboidRegionStatement.setInt(2, min.getBlockZ());
/*  884 */     insertCuboidRegionStatement.setInt(3, min.getBlockY());
/*  885 */     insertCuboidRegionStatement.setInt(4, min.getBlockX());
/*  886 */     insertCuboidRegionStatement.setInt(5, max.getBlockZ());
/*  887 */     insertCuboidRegionStatement.setInt(6, max.getBlockY());
/*  888 */     insertCuboidRegionStatement.setInt(7, max.getBlockX());
/*      */ 
/*  890 */     insertCuboidRegionStatement.execute();
/*      */   }
/*      */ 
/*      */   private void insertRegionPoly2D(ProtectedPolygonalRegion region) throws SQLException {
/*  894 */     insertRegion(region, "poly2d");
/*      */ 
/*  896 */     PreparedStatement insertPoly2dRegionStatement = this.conn.prepareStatement("INSERT INTO `region_poly2d` (`region_id`, `world_id`, `max_y`, `min_y` ) VALUES (?, " + this.worldDbId + ", ?, ?)");
/*      */ 
/*  905 */     insertPoly2dRegionStatement.setString(1, region.getId().toLowerCase());
/*  906 */     insertPoly2dRegionStatement.setInt(2, region.getMaximumPoint().getBlockY());
/*  907 */     insertPoly2dRegionStatement.setInt(3, region.getMinimumPoint().getBlockY());
/*      */ 
/*  909 */     insertPoly2dRegionStatement.execute();
/*      */ 
/*  911 */     updatePoly2dPoints(region);
/*      */   }
/*      */ 
/*      */   private void updatePoly2dPoints(ProtectedPolygonalRegion region) throws SQLException {
/*  915 */     PreparedStatement clearPoly2dPointsForRegionStatement = this.conn.prepareStatement("DELETE FROM `region_poly2d_point` WHERE `region_id` = ? AND `world_id` = " + this.worldDbId);
/*      */ 
/*  921 */     clearPoly2dPointsForRegionStatement.setString(1, region.getId().toLowerCase());
/*      */ 
/*  923 */     clearPoly2dPointsForRegionStatement.execute();
/*      */ 
/*  925 */     PreparedStatement insertPoly2dPointStatement = this.conn.prepareStatement("INSERT INTO `region_poly2d_point` (`id`, `region_id`, `world_id`, `z`, `x` ) VALUES (null, ?, " + this.worldDbId + ", ?, ?)");
/*      */ 
/*  935 */     String lowerId = region.getId();
/*  936 */     for (BlockVector2D point : region.getPoints()) {
/*  937 */       insertPoly2dPointStatement.setString(1, lowerId);
/*  938 */       insertPoly2dPointStatement.setInt(2, point.getBlockZ());
/*  939 */       insertPoly2dPointStatement.setInt(3, point.getBlockX());
/*      */ 
/*  941 */       insertPoly2dPointStatement.execute();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void insertRegionGlobal(GlobalProtectedRegion region) throws SQLException {
/*  946 */     insertRegion(region, "global");
/*      */   }
/*      */ 
/*      */   private void updateRegion(ProtectedRegion region, String type) throws SQLException {
/*  950 */     PreparedStatement updateRegionStatement = this.conn.prepareStatement("UPDATE `region` SET `priority` = ? WHERE `id` = ? AND `world_id` = " + this.worldDbId);
/*      */ 
/*  955 */     updateRegionStatement.setInt(1, region.getPriority());
/*  956 */     updateRegionStatement.setString(2, region.getId().toLowerCase());
/*      */ 
/*  958 */     updateRegionStatement.execute();
/*      */ 
/*  960 */     updateFlags(region);
/*      */ 
/*  962 */     updatePlayerAndGroups(region, Boolean.valueOf(false));
/*  963 */     updatePlayerAndGroups(region, Boolean.valueOf(true));
/*      */   }
/*      */ 
/*      */   private void updateRegionCuboid(ProtectedCuboidRegion region) throws SQLException {
/*  967 */     updateRegion(region, "cuboid");
/*      */ 
/*  969 */     PreparedStatement updateCuboidRegionStatement = this.conn.prepareStatement("UPDATE `region_cuboid` SET `min_z` = ?, `min_y` = ?, `min_x` = ?, `max_z` = ?, `max_y` = ?, `max_x` = ? WHERE `region_id` = ? AND `world_id` = " + this.worldDbId);
/*      */ 
/*  981 */     BlockVector min = region.getMinimumPoint();
/*  982 */     BlockVector max = region.getMaximumPoint();
/*      */ 
/*  984 */     updateCuboidRegionStatement.setInt(1, min.getBlockZ());
/*  985 */     updateCuboidRegionStatement.setInt(2, min.getBlockY());
/*  986 */     updateCuboidRegionStatement.setInt(3, min.getBlockX());
/*  987 */     updateCuboidRegionStatement.setInt(4, max.getBlockZ());
/*  988 */     updateCuboidRegionStatement.setInt(5, max.getBlockY());
/*  989 */     updateCuboidRegionStatement.setInt(6, max.getBlockX());
/*  990 */     updateCuboidRegionStatement.setString(7, region.getId().toLowerCase());
/*      */ 
/*  992 */     updateCuboidRegionStatement.execute();
/*      */   }
/*      */ 
/*      */   private void updateRegionPoly2D(ProtectedPolygonalRegion region) throws SQLException {
/*  996 */     updateRegion(region, "poly2d");
/*      */ 
/*  998 */     PreparedStatement updatePoly2dRegionStatement = this.conn.prepareStatement("UPDATE `region_poly2d` SET `max_y` = ?, `min_y` = ? WHERE `region_id` = ? AND `world_id` = " + this.worldDbId);
/*      */ 
/* 1006 */     updatePoly2dRegionStatement.setInt(1, region.getMaximumPoint().getBlockY());
/* 1007 */     updatePoly2dRegionStatement.setInt(2, region.getMinimumPoint().getBlockY());
/* 1008 */     updatePoly2dRegionStatement.setString(3, region.getId().toLowerCase());
/*      */ 
/* 1010 */     updatePoly2dRegionStatement.execute();
/*      */ 
/* 1012 */     updatePoly2dPoints(region);
/*      */   }
/*      */ 
/*      */   private void updateRegionGlobal(GlobalProtectedRegion region) throws SQLException {
/* 1016 */     updateRegion(region, "global");
/*      */   }
/*      */ 
/*      */   public Map<String, ProtectedRegion> getRegions()
/*      */   {
/* 1021 */     return this.regions;
/*      */   }
/*      */ 
/*      */   public void setRegions(Map<String, ProtectedRegion> regions)
/*      */   {
/* 1026 */     this.regions = regions;
/*      */   }
/*      */ 
/*      */   protected Object sqlUnmarshal(String rawValue) {
/*      */     try {
/* 1031 */       return this.yaml.load(rawValue); } catch (YAMLException e) {
/*      */     }
/* 1033 */     return String.valueOf(rawValue);
/*      */   }
/*      */ 
/*      */   protected String sqlMarshal(Object rawObject)
/*      */   {
/* 1038 */     return this.yaml.dump(rawObject);
/*      */   }
/*      */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.MySQLDatabase
 * JD-Core Version:    0.6.2
 */
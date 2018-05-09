package com.briup.base.jdbc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Serializable;
/**
 * 用于根据pojo类的src路径 ("src/main/java/com/briup/bean/Pojo.java")<br>
 * 自动创建dao层接口 和实现类 <br>
 * 自动创建service层接口 和实现类<br>
 * 
 * eg: 
 *  CreateBaseDaoAndService base = new CreateBaseDaoAndService("dao1","service1",true);<br>
 *	base.create("src/main/java/com/briup/estore/bean/Pojo.java","java.lang.Long");<br>
 * <br>
 * <b>注:</b>传入的可以是任意pojo.java文件的带src路径
 * */
public class CreateBaseDaoAndService {
	private  String DAO = "dao1";
	private  String SERVICE = "service1";
	private  Boolean useSpring = false;
	//创建sqlsession的代码
	//private String createSqlSession=null;
	private Boolean coverageDaoAndService= false;
	
	/**
	 * 
	 * */
	public CreateBaseDaoAndService() {
		super();
		
	}

	/**
	 * @param dao dao层的包名 
	 * 	<li> eg : dao 就会生成名字为dao的包
	 * @param service service层的包名
	 *  <li> eg : service 就会生成名字为service的包 
	 *  @param useSpring 项目是否使用了Spring 
	 *  <li>使用了spring ture
	 *  <li>没有使用  spring  false
	 * 
	 * */
	public CreateBaseDaoAndService(String dao, String service,Boolean useSpring,Boolean coverageDaoAndService) {
		super();
		DAO = dao;
		SERVICE = service;
		this.useSpring = useSpring;
		this.coverageDaoAndService = coverageDaoAndService;
	}
/*	public CreateBaseDaoAndService(String dao, String service,Boolean useSpring,String createSqlSession,Boolean coverageDaoAndService) {
		super();
		DAO = dao;
		SERVICE = service;
		this.useSpring = useSpring;
		this.createSqlSession = createSqlSession;
		this.coverageDaoAndService = coverageDaoAndService;
	}
*/
	/**
	 * 读取包下的pojo类<br>
	 * 
	 * Map<pojo类名,>
	 * 
	 * @param beanFileQualifiedName 某一个pojo.java文件的项目下路径
	 * 	<li>  src/main/java/com/briup/bean/Pojo.java
	 * @param id 主键类型.class
	 * 
	 */
	public void create(String beanFileQualifiedName,Class<? extends Serializable> id){
		//得到全限定名
		String idQualifiedName = id.toString().substring(6);
		try {
			File fileroot = new File("");
			//当前项目的根目录 : F:\Briup\Briup_Work_space\sxdx\workspace\jd1802
			String path = fileroot.getCanonicalPath(); 
			File filepojo = new File(path,beanFileQualifiedName);
			//System.out.println("pojo类 的 路径  : "+filepojo.getPath());
			//父目录
			String parent = filepojo.getParent();
			File file = new File(parent);
			//System.out.println("当前父目录 : "+file);
			
			
			//创建dao接口包 成功
			String iDao = file.getParent()+"\\"+DAO;
			File iDaoFile = new File(iDao);
			iDaoFile.mkdir();
			//创建dao实现类包
			String iDaoImpl = iDaoFile.getPath()+"\\impl";
			File iDaoImplFile = new File(iDaoImpl);
			//System.out.println("实现类路径 "+iDaoImplFile);
			iDaoImplFile.mkdir();
			//创建service接口包
			String iService = file.getParent()+"\\"+SERVICE;
			File iServiceFile = new File(iService);
			iServiceFile.mkdir();
			//创建service接口实现类包
			String iServiceImpl = iServiceFile.getPath()+"\\impl";
			File iServiceImplFile = new File(iServiceImpl);
			iServiceImplFile.mkdir();
			
			// 是否是目录 
			boolean directory = file.isDirectory();
			if (directory) {
				// 所有的子文件
				File[] listFiles = file.listFiles();
				for (File pojoFile : listFiles) {
					String pojoName = pojoFile.getName().substring(0, pojoFile.getName().length()-5);
					FileInputStream fis = null;
					fis = new FileInputStream(pojoFile);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
					//读取的字符串
					String date = null;
					boolean flag = true;
					//获得当前pojo类的全限定名
					String qualifiedName =null;
					while(flag){
						date = br.readLine();
						if(date.length()>0){
							//读取到pojo类的 全限定名
							String packageLine = date.trim().substring(8).trim();
							//获得当前pojo类的全限定名
							qualifiedName = packageLine.substring(0, packageLine.length()-1).concat(".")+pojoName;
							flag = false;
							break;
						}else{
							continue;
						}
					}
					br.close();
					fis.close();
					
					//System.out.println("类名 : |"+pojoName+"----------全限定名: |"+qualifiedName);
				//	String itemPackage = filepojo.getPath().substring(0,filepojo.getPath().lastIndexOf("\\"));
					//基础包 : F:\Briup\Briup_Work_space\sxdx\workspace\jd1802\src\main\java\com\briup\estore
					//String basePackage = itemPackage.substring(0, itemPackage.lastIndexOf("\\"));
					
					//创建IDao
					String iDaoQualifiedName = createIDao(qualifiedName, iDaoFile, idQualifiedName);
					//创建DaoImpl
					createDaoImpl(qualifiedName,iDaoQualifiedName,iDaoImplFile,idQualifiedName);
					//创建Iservice
					String iServiceQualifiedName =  createIService(qualifiedName,iServiceFile,idQualifiedName);
					
					//创建ServiceImpl
					createServiceImpl(qualifiedName,iServiceQualifiedName,iServiceImplFile,idQualifiedName,iDaoQualifiedName);
					
				}
			} else {
				throw new Exception("这个不是文件夹路径");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建service层实现类
	 * @param qualifiedName pojo 的全限定名
	 * @param iServiceQualifiedName service层接口的全限定名
	 * @param iServiceImplFile 实现类的文件夹
	 * @param idQualifiedName 主键的全限定名
	 * @param iDaoQualifiedName 对应dao层接口全限定名
	 * 正常使用 有效
	 * */
	private void createServiceImpl(String qualifiedName, String iServiceQualifiedName, File iServiceImplFile,
			String idQualifiedName,String iDaoQualifiedName) {
		try{
			//pojo类名 : ShopCart
			String pojoName = qualifiedName.substring(qualifiedName.lastIndexOf(".")+1);
			
			//接口名
			String iPojoService = iServiceQualifiedName.substring(iServiceQualifiedName.lastIndexOf(".")+1);
			//com.briup.estore.bean
			String beanPackage = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
			//com.briup.estore.service1
			String serviceImplPackage = beanPackage.substring(0, beanPackage.lastIndexOf(".")).concat("."+SERVICE+".impl");
			//当前接口所在的page : 
			String pojoFileName = pojoName+"ServiceImpl";
			
			File pojoDaoImpl = new File(iServiceImplFile,pojoFileName+".java");
			
			boolean isFile = pojoDaoImpl.isFile();
			if(isFile &&  (false == coverageDaoAndService)){
				return ;
			}
			
			pojoDaoImpl.createNewFile();
			FileWriter fw = new FileWriter(pojoDaoImpl);
			fw.append("package "+serviceImplPackage+";\r\n");
			fw.append("\r\n");
			if(useSpring){
				fw.append("import org.springframework.beans.factory.annotation.Autowired;\r\n");
				fw.append("import org.springframework.stereotype.Service;\r\n");
				fw.append("import "+iDaoQualifiedName+";\r\n");
			}else{
				fw.append("import "+iDaoQualifiedName.substring(0,iDaoQualifiedName.lastIndexOf("."))+".impl."+pojoName+"DaoImpl;\r\n");
			}
			fw.append("import "+qualifiedName+";\r\n");
			fw.append("import "+iServiceQualifiedName+";\r\n");
			fw.append("import com.briup.base.jdbc.dao.IBaseDao;\r\n");
			fw.append("import com.briup.base.jdbc.service.BaseServiceImpl;\r\n");
			fw.append("\r\n");
			if(useSpring){
				fw.append("@Service\r\n");
			}
			fw.append("public class "+pojoFileName+" extends BaseServiceImpl<"+pojoName+","+idQualifiedName+"> implements "+iPojoService+"{\r\n");
			
			if(useSpring){
				fw.append("@Autowired\r\n");
				fw.append("private "+iDaoQualifiedName.substring(iDaoQualifiedName.lastIndexOf(".")+1)+" dao;\r\n");
			}
			
			fw.append("\t@Override\r\n");
			fw.append("\tpublic IBaseDao<"+pojoName+","+idQualifiedName+"> getDao() {\r\n");
			if(useSpring){
				fw.append("\t\treturn dao;\r\n");
			}else{
				fw.append("\t\treturn new "+pojoName+"DaoImpl();\r\n");
			}
			fw.append("\t}");
			fw.append("\r\n}");
	
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建IService
	 * @param qualifiedName 当前类的全限定名
	 * 	<li>com.briup.estore.bean.Pojo
	 * @param iDaoFile Dao层接口所在文件
	 * 
	 * @param idQualifiedName 主键权限的名 java.lang.Long
	 * @return 返回当前接口的全限定名
	 * */
	private String createIService(String qualifiedName, File iServiceFile, String idQualifiedName) {
		//pojo类名 : ShopCart
		String pojoName = qualifiedName.substring(qualifiedName.lastIndexOf(".")+1);
		
		//com.briup.estore.bean
		String beanPackage = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
		//com.briup.estore.service1
		String servicePackage = beanPackage.substring(0, beanPackage.lastIndexOf(".")).concat("."+SERVICE);
		
		//当前接口所在的page : 
		//System.out.println("path : "+daoPackage);
		String pojoFileName = "I"+pojoName+"Service";
		try {
			File iPojoService = new File(iServiceFile,pojoFileName+".java");
			
			boolean isFile = iPojoService.isFile();
			if(isFile &&  (false == coverageDaoAndService)){
				return servicePackage+"."+pojoFileName;
			}
			
			iPojoService.createNewFile();
			FileWriter fw= new FileWriter(iPojoService);
			
			fw.append("package "+servicePackage+";\r\n");
			fw.append("\r\n");
			fw.append("import "+qualifiedName+";\r\n");
			fw.append("import com.briup.base.jdbc.service.IBaseService;\r\n");
			fw.append("\r\n");
			fw.append("public interface "+pojoFileName+" extends IBaseService<"+pojoName+","+idQualifiedName+">{\r\n");
			fw.append("\r\n");
			fw.append("\r\n}");
			fw.flush();
			fw.close();
			return servicePackage+"."+pojoFileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	/**
	 * 创建dao层实现类
	 * @param qualifiedName pojo 的全限定名
	 * @param iDaoQualifiedName dao层接口的全限定名
	 * @param iDaoImplFile 实现类的文件夹
	 * @param idQualifiedName 主键的全限定名
	 * 正常使用 有效
	 * */
	private void createDaoImpl(String qualifiedName,String iDaoQualifiedName, File iDaoImplFile, String idQualifiedName) {
		try {
			//pojo类名 : ShopCart
			String pojoName = qualifiedName.substring(qualifiedName.lastIndexOf(".")+1);
			
			//接口名
			String iPojoDao = iDaoQualifiedName.substring(iDaoQualifiedName.lastIndexOf(".")+1);
			//com.briup.estore.bean
			String beanPackage = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
			//com.briup.estore.dao1
			String daoImplPackage = beanPackage.substring(0, beanPackage.lastIndexOf(".")).concat("."+DAO+".impl");
			//System.out.println("实现类的dao "+daoImplPackage);
			//当前接口所在的page : 
			//System.out.println("path : "+daoPackage);
			String pojoFileName = pojoName+"DaoImpl";
			//System.out.println("实现类名 ; "+pojoFileName);
			
			File pojoDaoImpl = new File(iDaoImplFile,pojoFileName+".java");
			boolean isFile = pojoDaoImpl.isFile();
			if(isFile &&  (false == coverageDaoAndService)){
				return;
			}
			
			pojoDaoImpl.createNewFile();
			FileWriter fw = new FileWriter(pojoDaoImpl);
			fw.append("package "+daoImplPackage+";\r\n");
			fw.append("\r\n");
			fw.append("import "+qualifiedName+";\r\n");
			fw.append("import java.sql.Connection;\r\n\r\n");
			if(useSpring){
				//fw.append("import  org.apache.ibatis.session.SqlSessionFactory;\r\n");
				fw.append("import javax.sql.DataSource;\r\n\r\n");
				fw.append("import org.springframework.jdbc.datasource.DataSourceUtils;\r\n\r\n");
				fw.append("import org.springframework.beans.factory.annotation.Autowired;\r\n");
				fw.append("import org.springframework.stereotype.Repository;\r\n");
			}else{
				//fw.append("import static "+createSqlSession.substring(0, createSqlSession.lastIndexOf("("))+";\r\n");
			}
			fw.append("import com.briup.base.jdbc.dao.BaseDaoImpl;\r\n");
			fw.append("import "+iDaoQualifiedName+";\r\n");
			fw.append("\r\n");
			if(useSpring){
				fw.append("@Repository\r\n");
			}
			fw.append("public class "+pojoFileName+" extends BaseDaoImpl<"+pojoName+","+idQualifiedName+"> implements "+iPojoDao+"{\r\n");
			if(useSpring){
				fw.append("@Autowired\r\n");
				fw.append("private DataSource dataSource;\r\n");
			}	
/*			if(useSpring){
				fw.append("@Autowired\r\n");
				fw.append("private SqlSessionFactory factory;\r\n");
			}	
*/			fw.append("\t@Override\r\n");
			fw.append("\tpublic Connection getConnection() {\r\n");
			if(useSpring){
				fw.append("\t\treturn DataSourceUtils.getConnection(dataSource);\r\n");
			}else{
				//fw.append("\t\treturn "+createSqlSession.substring(createSqlSession.lastIndexOf(".")+1)+";\r\n");
				fw.append("\t\treturn null;\r\n");
				
			}
			fw.append("\t}");
			fw.append("\r\n}");

			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建dao层代码
	 * @param qualifiedName 当前类的全限定名
	 * 	<li>com.briup.estore.bean.Pojo
	 * @param iDaoFile Dao层接口所在文件
	 * 
	 * @param idQualifiedName 主键权限的名 java.lang.Long
	 * @return 返回当前接口的全限定名
	 * 正常使用 有效
	 * */
	private String createIDao(String qualifiedName,File iDaoFile,String idQualifiedName){
		//pojo类名 : ShopCart
		String pojoName = qualifiedName.substring(qualifiedName.lastIndexOf(".")+1);
		
		//com.briup.estore.bean
		String beanPackage = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
		//com.briup.estore.dao1.impl
		String daoPackage = beanPackage.substring(0, beanPackage.lastIndexOf(".")).concat("."+DAO);
		
		//当前接口所在的page : 
		//System.out.println("path : "+daoPackage);
		String pojoFileName = "I"+pojoName+"Dao";
		try {
			File iPojoDao = new File(iDaoFile,pojoFileName+".java");
			
			boolean isFile = iPojoDao.isFile();
			//有文件 true   没有文件 false
			//coverageDaoAndService true 覆盖
			if(isFile &&  (false == coverageDaoAndService)){
				return daoPackage+"."+pojoFileName;
			}
			
			iPojoDao.createNewFile();
			FileWriter fw= new FileWriter(iPojoDao);
			fw.append("package "+daoPackage+";");
			fw.append("\r\n");
			fw.append("import "+qualifiedName+";\r\n");
			fw.append("import com.briup.base.jdbc.dao.IBaseDao;\r\n");
			fw.append("\r\n");
			fw.append("public interface "+pojoFileName+" extends IBaseDao<"+pojoName+","+idQualifiedName+">{\r\n\r\n}");
			fw.flush();
			fw.close();
			return daoPackage+"."+pojoFileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/*public static void maina(String[] args) {
		//File file = new File("F:\\Briup\\Briup_Work_space\\sxdx\\workspace\\jd1802\\src\\main\\java\\com\\briup\\estore\\dao");
		File file = new File("F:/Briup/Briup_Work_space/sxdx/workspace/jd1802/src/main/java/com/briup/estore/dao12");
		
		file.mkdir();
	}*/

	
	
	
}

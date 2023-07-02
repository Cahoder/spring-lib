import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

import javax.swing.*

/*
 * 注意事项：
 * 默认会在选择的目录下新建po、dao、service，然后生成对应的类，xml会生成在 resources/dao 下
 * 如果需要自动生成的调整命名，可以直接修改脚本（如脚本里面将 baseDAOClassSuffix="DAO" 修改为：baseDAOClassSuffix="Mapper"）
 * 自动生成的PO会使用lombok相关注解，idea请安装lombok-plugin插件，pom中引入对应依赖：
   <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
 *
 * 使用方法：
 * 1. 使用idea连接数据库
 * 2. 复制本lib包下的/META-INF/script/AutoGeneratePojo.groovy 脚本文件
 * 3. 选择表，右键 Scripted Extensions -> GO to Scripts Directory，将脚本文件粘贴到该目录（schema）下
 * 4. 选择表，右键 Scripted Extensions -> AutoGeneratePojo.groovy 选择maven项目下的目录即可（建议包所在目录 copy path，然后直接填入而不用选择）
 * 5. 选择目录后，会询问是否同时生成Service，如果选择“是”则会生成PO、DAO+XML、Service；否则仅生成PO、DAO+XML
 * 6. src目录右键-> Synchronize src 刷新目录
 */

javaTypeMapping = [
        (~/(?i)bigint/)                               : "Long",
        (~/(?i)decimal/)                              : "BigDecimal",
        (~/(?i)int|tinyint|smallint|mediumint|number/): "Integer",
        (~/(?i)bool|bit/)                             : "Boolean",
        (~/(?i)float|double|real/)                    : "Double",
        (~/(?i)date/)                                 : "Date",
        (~/(?i)time/)                                 : "Time",
        (~/(?i)datetime|timestamp/)                   : "TIMESTAMP",
        (~/(?i)blob|binary|bfile|clob|raw|image|text/): "InputStream",
        (~/(?i)/)                                     : "String"
]

myBatisTypeMapping = [
        (~/(?i)bigint/)                               : "BIGINT",
        (~/(?i)float|double|real/)                    : "DECIMAL",
        (~/(?i)tinyint/)                              : "TINYINT",
        (~/(?i)int|smallint|mediumint|number/)        : "INTEGER",
        (~/(?i)bool|bit/)                             : "BOOLEAN",
        (~/(?i)decimal/)                              : "DECIMAL",
        (~/(?i)datetime|timestamp|date|time/)         : "DATE",
        (~/(?i)blob|binary|bfile|clob|raw|image|text/): "BLOB",
        (~/(?i)/)                                     : "VARCHAR"
]

importMapping = [
        "BigDecimal" : "java.math.BigDecimal",
        "Date"       : "java.util.Date",
        "InputStream": "java.io.InputStream",
]

poPackageNameSuffix = "po" // com.xxx.user.po
poClassSuffix = "Po" // UserPO
boClassSuffix = "Bo"

baseDAOPackage = "dao" // com.xxx.user.dao
baseDAOClassSuffix = "Dao" // UserDao
daoResourceXmlDir = "\\dao" // resource/dao

servicePackageSuffix = "service.db" // com.xxx.user.service
serviceClassNameSuffix = "DbService" // UserService

serviceImplPackageSuffix = "service.db.impl" // com.xxx.user.service.impl
serviceImplClassNameSuffix = "DbServiceImpl"  // UserServiceImpl

baseMapper = "BaseDao"
baseMapperPackage = "org.spring.lib.ibatis.dao.base"

// 基础Service
baseService = "BaseService"
baseServicePackage = "org.spring.lib.ibatis.service"

encoding = "UTF-8"

xmlTemplate = '<?xml version="1.0" encoding="' + encoding + '" ?>\n' +
        '<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >\n' +
        '<mapper namespace="%s">\n\n' +
        '    <sql id="Base_Column_List">\n' +
        '        %s\n' +
        '    </sql>\n\n' +
        '    <resultMap id="BaseResultMap" type="%s">\n' +
        '        %s' +
        '    </resultMap>\n\n' +
        '</mapper>'

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    if (!dir.toString().contains('\\src\\main\\java\\')) {
        throw new GenerateFailedException("please choose maven project's directory in: /src/main/java/.");
    }

    int showConfirmDialog = JOptionPane.showConfirmDialog(null, "generate with dao + Service?", "Po are ready", JOptionPane.YES_NO_OPTION);
    if (JOptionPane.CLOSED_OPTION == showConfirmDialog) {
        throw new GenerateFailedException("You have canceled.");
    }

    generateServiceFlag = showConfirmDialog == JOptionPane.YES_OPTION
    SELECTION.filter { it instanceof DasTable && it.getKind() == ObjectKind.TABLE }.each {
        generate(it, dir, generateServiceFlag)
    }
}

def generate(table, dir, generateServiceFlag) {
    String dirStr = dir.toString()

    // 生成po
    String boClassName = getClassNameByTableName(table.getName(), boClassSuffix)
    String poClassName = getClassNameByTableName(table.getName(), poClassSuffix)
    def fields = calcFields(table)
    surePrimaryKey(fields, table)
    def poDirStr = dirStr + getDirByPackageName(poPackageNameSuffix)
    String poPackage = getPoPackageName(poDirStr)
    File file = getFile(poDirStr, "${poClassName}.java")
    file.withPrintWriter(encoding) { out -> generatePO(out, table, fields, poClassName, poPackage, boClassName) }

    // 生成BO对象
    file = getFile(poDirStr, "${boClassName}.java")
    file.withPrintWriter(encoding) { out -> generateBO(out, table, fields, boClassName, poPackage) }

    def poClassPrefix = poClassName.substring(0, poClassName.length() - poClassSuffix.size())
    def packagePrefix = poPackage.substring(0, poPackage.size() - poPackageNameSuffix.size())

    // 生成DAO的XML
    def daoPackage = "${packagePrefix}${baseDAOPackage}"
    def daoClassName = "${poClassPrefix}${baseDAOClassSuffix}"

    def path = getResourcePath(dirStr)
    file = getFile("${path}${daoResourceXmlDir}", daoClassName + ".xml")
    file.withPrintWriter(encoding) { out -> generateXml(out, fields, daoPackage, daoClassName, poPackage + "." + poClassName) }

    if (!generateServiceFlag) {
        return
    }

    // 生成DAO
    file = getFile(dirStr + getDirByPackageName(baseDAOPackage), daoClassName + ".java")
    file.withPrintWriter(encoding) { out -> generateDAO(out, table, fields, daoPackage, daoClassName, poPackage, poClassName) }

    // 生成service
    def servicePackage = "${packagePrefix}${servicePackageSuffix}"
    def serviceClassName = "${poClassPrefix}${serviceClassNameSuffix}"
    file = getFile(dirStr + getDirByPackageName(servicePackageSuffix), serviceClassName + ".java")
    file.withPrintWriter(encoding) { out -> generateService(out, table, fields, servicePackage, serviceClassName, poPackage, poClassName) }

    // 生成serviceImpl
    def serviceImplPackage = "${packagePrefix}${serviceImplPackageSuffix}"
    def serviceImplClassName = "${poClassPrefix}${serviceImplClassNameSuffix}"
    file = getFile(dirStr + getDirByPackageName(serviceImplPackageSuffix), serviceImplClassName + ".java")
    file.withPrintWriter(encoding) { out ->
        generateServiceImpl(out, table, fields,
                serviceImplPackage, serviceImplClassName,
                servicePackage, serviceClassName,
                daoPackage, daoClassName,
                poPackage, poClassName)
    }

}

def getDirByPackageName(String packageName) {
    def result = packageName.replace('.', "\\")
    if (result.startsWith("\\")) {
        return result
    }
    return "\\" + result
}

def generateServiceImpl(out, table, fields,
                        serviceImplPackage, serviceImplClassName,
                        servicePackage, serviceClassName,
                        daoPackage, daoClassName,
                        poPackage, poClassName) {
    def serviceClassStr = ""
    def id = fields.find { it -> it["isId"] }
    serviceClassStr += "package $serviceImplPackage;\n\n"
    serviceClassStr += "import ${daoPackage}.${daoClassName};\n"
    serviceClassStr += "import ${poPackage}.${poClassName};\n"
    serviceClassStr += "import ${servicePackage}.${serviceClassName};\n"
    serviceClassStr += "import ${baseServicePackage}.impl.${baseService}Impl;\n"
    serviceClassStr += "import org.springframework.stereotype.Service;\n\n"
    serviceClassStr += "import javax.annotation.Resource;\n\n"
    serviceClassStr += classComment(table.getComment()) + "\n"
    serviceClassStr += "@Service\n"
    serviceClassStr += "public class ${serviceImplClassName} extends ${baseService}Impl<${poClassName}, ${id.type}> implements ${serviceClassName} {\n\n"
    serviceClassStr += "    @Resource\n"
    def daoProperty = daoClassName[0].toLowerCase() + daoClassName.substring(1)
    serviceClassStr += "    private ${daoClassName} ${daoProperty};\n\n"
    serviceClassStr += "}"
    out.print serviceClassStr
}

def generateService(out, table, fields, servicePackage, serviceClassName, poPackage, poClassName) {
    def serviceClassStr = ""
    def id = fields.find { it -> it["isId"] }
    serviceClassStr += "package $servicePackage;\n\n"
    serviceClassStr += "import ${poPackage}.${poClassName};\n"
    serviceClassStr += "import ${baseServicePackage}.${baseService};\n\n"
    serviceClassStr += classComment(table.getComment()) + "\n"
    serviceClassStr += "public interface ${serviceClassName} extends ${baseService}<${poClassName}, ${id.type}> {\n"
    serviceClassStr += "}"
    out.print serviceClassStr
}

def generateXml(out, fields, daoPackage, daoClassName, poClassName) {
    def columnList = ''
    def i = 0
    fields.each() {
        columnList += "${it['column']}, "
        if (++i % 8 == 0) {
            columnList += "\n        "
        }
    }
    columnList = columnList.replaceAll('\n\\s+$', '')
    if (columnList != '') {
        columnList = columnList[0..columnList.size() - 3]
    }
    out.print getXml("${daoPackage}.${daoClassName}", columnList, fields, poClassName)
}

def generateDAO(out, table, fields, daoPackage, daoClassName, poPackage, poClassName) {
    def daoClassStr = ""
    def id = fields.find { it -> it["isId"] }
    daoClassStr += "package $daoPackage;\n\n"
    daoClassStr += "import ${poPackage}.${poClassName};\n"
    daoClassStr += "import ${baseMapperPackage}.${baseMapper};\n\n"
    daoClassStr += classComment(table.getComment()) + "\n"
    daoClassStr += "public interface ${daoClassName} extends ${baseMapper}<${poClassName}, ${id.type}> {\n"
    daoClassStr += "}"
    out.print daoClassStr
}

def generatePO(out, table, fields, className, packageName, boClassName) {
    def poClassStr = ""
    poClassStr += "package $packageName;\n"
    poClassStr += "\nimport lombok.Data;\n\n"
    Set set = new HashSet()
    fields.each() {
        set.add(it.type)
    }
    boolean isImport = false
    set.each {
        if (importMapping[it] != null) {
            isImport = true
            poClassStr += "import " + importMapping[it] + ";\n"
        }
    }
    if (isImport) {
        poClassStr += "\n"
    }

    if (table.getComment() != null && table.getComment() != '') {
        poClassStr += classComment(table.getComment()) + "\n"
    } else {
        poClassStr += classComment() + "\n"
    }

    poClassStr += "@Data\n"
    poClassStr += "public class $className {\n\n"

    fields.each() {
        def comment
        if (it.commoent != null && it.commoent != "") {
            comment = it.commoent
        } else {
            comment = it.column
        }
        poClassStr += "    /**\n     * ${comment}\n     */\n"
        poClassStr += "    private ${it.type} ${it.name};\n\n"
    }
    if (poClassStr.endsWith("\n\n")) {
        poClassStr = poClassStr.substring(0, poClassStr.length() - 1)
    }
    poClassStr += "}"
    out.print poClassStr
}

def generateBO(out, table, fields, className, packageName) {
    def boClassStr = ""
    boClassStr += "package $packageName;\n"
    boClassStr += "\nimport lombok.Data;"
    boClassStr += "\nimport java.io.Serializable;"
    boClassStr += "\nimport io.swagger.annotations.*;\n\n"
    Set set = new HashSet()
    fields.each() {
        set.add(it.type)
    }
    boolean isImport = false
    set.each {
        if (importMapping[it] != null) {
            isImport = true
            boClassStr += "import " + importMapping[it] + ";\n"
        }
    }
    if (isImport) {
        boClassStr += "\n"
    }
    if (table.getComment() != null && table.getComment() != '') {
        boClassStr += classComment(table.getComment()) + "\n"
        boClassStr += "@ApiModel(\"" + table.getComment() + "\")\n"
    } else {
        boClassStr += classComment() + "\n"
        boClassStr += "@ApiModel\n"
    }
    boClassStr += "@Data\n"
    boClassStr += "public class $className implements Serializable {\n\n"

    boClassStr += "    // TODO: please generate the serialVersionUID constant " +
            "and move this class to api module.\n\n"

    fields.each() {
        def comment
        if (it.commoent != null && it.commoent != "") {
            comment = it.commoent
        } else {
            comment = it.column
        }
        boClassStr += "    /**\n     * ${comment}\n     */\n"
        boClassStr += "    @ApiModelProperty(\"${comment}\")\n"
        boClassStr += "    private ${it.type} ${it.name};\n\n"
    }
    if (boClassStr.endsWith("\n\n")) {
        boClassStr = boClassStr.substring(0, boClassStr.length() - 1)
    }
    boClassStr += "}"
    out.print boClassStr
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = javaTypeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           isId    : DasUtil.isPrimary(col),
                           column  : col.getName(),
                           name    : camel(col.getName()),
                           type    : typeStr,
                           dbtype  : spec,
                           commoent: col.getComment()
                   ]]
    }
}

def surePrimaryKey(fields, table) {
    def id = fields.find { it -> it["isId"] }
    if (id == null) {
        throw new GenerateFailedException("not found primary key in the table: [" + table.getName() + "]");
    }
}

def getPoPackageName(String str) {
    def srcStr = '\\src\\main\\java\\'
    def index = str.indexOf(srcStr) + srcStr.size()
    return str.substring(index, str.size()).replace("\\", ".")
}

def getClassNameByTableName(String tableName, String suffix) {
    if (tableName.startsWith('t_')) {
        tableName = tableName[2..tableName.size() - 1]
    }
    tableName = camel(tableName);
    return tableName[0].toUpperCase() + tableName[1..tableName.size() - 1] + suffix;
}

def camel(String underline) {
    String camelStr = ''
    underline.split('_').each { camelStr += it[0].toUpperCase() + it[1..it.size() - 1] }
    return camelStr[0].toLowerCase() + camelStr.substring(1)
}

File getFile(String dirtStr, fileName) {
    File file = new File(dirtStr + '/' + fileName)
    File pFile = file.getParentFile();
    if (!pFile.exists()) {
        pFile.mkdirs()
    }
    return file
}

def dateStr() {
    Date date = new Date();
    return 1900+date.getYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()
}

def classComment(String... strs) {
    String comment = '/**\n'
    strs.each() {
        comment += ' * ' + it + '\n'
    }
    comment += ' *\n'
    comment += ' * @author groovy script\n' +
            ' * @version 1.0\n' +
            ' * @since ' + dateStr() + "\n"
    comment += ' */'
}

def getResourcePath(dir) {
    int indexOf = dir.indexOf('\\src\\main\\java') + '\\src\\main'.length();
    return dir.substring(0, indexOf) + "\\resources"
}

def getXml(String daoClass, String fieldNames, fields, poClassName) {

    def xmlFields = ''

    fields.each() {
        def mybatisType = myBatisTypeMapping.find { p, t -> p.matcher("${it['dbtype']}").find() }.value
        if ("${it['name']}" == 'id') {
            xmlFields += '<id column="'+ it['column'] +'" jdbcType="'+ mybatisType +'" property="'+ it['name'] +'" />\n'
        } else {
            xmlFields +='        <result column="'+ it['column'] +'" jdbcType="'+ mybatisType +'" property="'+ it['name'] +'" />\n'
        }
    }
    return String.format(null, xmlTemplate, daoClass, fieldNames, poClassName, xmlFields)
}

class GenerateFailedException extends RuntimeException {
    GenerateFailedException(String var1) {
        super(var1)
    }
}
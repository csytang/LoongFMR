//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.09 at 10:24:49 AM PDT 
//


package net.gexf._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for defaultedgetype-type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="defaultedgetype-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="directed"/>
 *     &lt;enumeration value="undirected"/>
 *     &lt;enumeration value="mutual"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "defaultedgetype-type")
@XmlEnum
public enum DefaultedgetypeType {

    @XmlEnumValue("directed")
    DIRECTED("directed"),
    @XmlEnumValue("undirected")
    UNDIRECTED("undirected"),
    @XmlEnumValue("mutual")
    MUTUAL("mutual");
    private final String value;

    DefaultedgetypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DefaultedgetypeType fromValue(String v) {
        for (DefaultedgetypeType c: DefaultedgetypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

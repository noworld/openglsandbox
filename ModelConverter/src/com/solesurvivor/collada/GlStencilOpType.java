//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.30 at 03:22:43 PM EDT 
//


package com.solesurvivor.collada;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for gl_stencil_op_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="gl_stencil_op_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="KEEP"/>
 *     &lt;enumeration value="ZERO"/>
 *     &lt;enumeration value="REPLACE"/>
 *     &lt;enumeration value="INCR"/>
 *     &lt;enumeration value="DECR"/>
 *     &lt;enumeration value="INVERT"/>
 *     &lt;enumeration value="INCR_WRAP"/>
 *     &lt;enumeration value="DECR_WRAP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "gl_stencil_op_type")
@XmlEnum
public enum GlStencilOpType {

    KEEP,
    ZERO,
    REPLACE,
    INCR,
    DECR,
    INVERT,
    INCR_WRAP,
    DECR_WRAP;

    public String value() {
        return name();
    }

    public static GlStencilOpType fromValue(String v) {
        return valueOf(v);
    }

}

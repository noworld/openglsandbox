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
 * <p>Java class for fx_surface_face_enum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="fx_surface_face_enum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="POSITIVE_X"/>
 *     &lt;enumeration value="NEGATIVE_X"/>
 *     &lt;enumeration value="POSITIVE_Y"/>
 *     &lt;enumeration value="NEGATIVE_Y"/>
 *     &lt;enumeration value="POSITIVE_Z"/>
 *     &lt;enumeration value="NEGATIVE_Z"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "fx_surface_face_enum")
@XmlEnum
public enum FxSurfaceFaceEnum {

    POSITIVE_X,
    NEGATIVE_X,
    POSITIVE_Y,
    NEGATIVE_Y,
    POSITIVE_Z,
    NEGATIVE_Z;

    public String value() {
        return name();
    }

    public static FxSurfaceFaceEnum fromValue(String v) {
        return valueOf(v);
    }

}

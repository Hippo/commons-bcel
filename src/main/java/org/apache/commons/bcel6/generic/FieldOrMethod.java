/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.commons.bcel6.generic;

import org.apache.commons.bcel6.Const;
import org.apache.commons.bcel6.classfile.ConstantCP;
import org.apache.commons.bcel6.classfile.ConstantNameAndType;
import org.apache.commons.bcel6.classfile.ConstantPool;
import org.apache.commons.bcel6.classfile.ConstantUtf8;

/**
 * Super class for InvokeInstruction and FieldInstruction, since they have
 * some methods in common!
 *
 * @version $Id$
 */
public abstract class FieldOrMethod extends CPInstruction implements LoadClass {

    /**
     * Empty constructor needed for the Class.newInstance() statement in
     * Instruction.readInstruction(). Not to be used otherwise.
     */
    FieldOrMethod() {
    }


    /**
     * @param index to constant pool
     */
    protected FieldOrMethod(short opcode, int index) {
        super(opcode, index);
    }


    /** @return signature of referenced method/field.
     */
    public String getSignature( ConstantPoolGen cpg ) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantCP cmr = (ConstantCP) cp.getConstant(super.getIndex());
        ConstantNameAndType cnat = (ConstantNameAndType) cp.getConstant(cmr.getNameAndTypeIndex());
        return ((ConstantUtf8) cp.getConstant(cnat.getSignatureIndex())).getBytes();
    }


    /** @return name of referenced method/field.
     */
    public String getName( ConstantPoolGen cpg ) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantCP cmr = (ConstantCP) cp.getConstant(super.getIndex());
        ConstantNameAndType cnat = (ConstantNameAndType) cp.getConstant(cmr.getNameAndTypeIndex());
        return ((ConstantUtf8) cp.getConstant(cnat.getNameIndex())).getBytes();
    }


    /** 
     * @return name of the referenced class/interface
     * @deprecated If the instruction references an array class,
     *    this method will return "java.lang.Object".
     *    For code generated by Java 1.5, this answer is
     *    sometimes wrong (e.g., if the "clone()" method is
     *    called on an array).  A better idea is to use
     *    the {@link #getReferenceType()} method, which correctly distinguishes
     *    between class types and array types.
     *  
     */
    @Deprecated
    public String getClassName( ConstantPoolGen cpg ) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantCP cmr = (ConstantCP) cp.getConstant(super.getIndex());
        String className = cp.getConstantString(cmr.getClassIndex(), Const.CONSTANT_Class);
        if (className.startsWith("[")) {
            // Turn array classes into java.lang.Object.
            return "java.lang.Object";
        }
        return className.replace('/', '.');
    }


    /** @return type of the referenced class/interface
     * @deprecated If the instruction references an array class,
     *    the ObjectType returned will be invalid.  Use
     *    getReferenceType() instead.
     */
    @Deprecated
    public ObjectType getClassType( ConstantPoolGen cpg ) {
        return ObjectType.getInstance(getClassName(cpg));
    }


    /**
     * Return the reference type representing the class, interface,
     * or array class referenced by the instruction.
     * @param cpg the ConstantPoolGen used to create the instruction
     * @return an ObjectType (if the referenced class type is a class
     *   or interface), or an ArrayType (if the referenced class
     *   type is an array class)
     */
    public ReferenceType getReferenceType( ConstantPoolGen cpg ) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantCP cmr = (ConstantCP) cp.getConstant(super.getIndex());
        String className = cp.getConstantString(cmr.getClassIndex(), Const.CONSTANT_Class);
        if (className.startsWith("[")) {
            return (ArrayType) Type.getType(className);
        }
        className = className.replace('/', '.');
        return ObjectType.getInstance(className);
    }


    /** 
     * Get the ObjectType of the method return or field.
     * 
     * @return type of the referenced class/interface
     * @throws ClassGenException when the field is (or method returns) an array, 
     */
    @Override
    public ObjectType getLoadClassType( ConstantPoolGen cpg ) {
        ReferenceType rt = getReferenceType(cpg);
        if(rt instanceof ObjectType) {
            return (ObjectType)rt;
        }
        throw new ClassGenException(rt.getSignature() + " does not represent an ObjectType");
    }
}

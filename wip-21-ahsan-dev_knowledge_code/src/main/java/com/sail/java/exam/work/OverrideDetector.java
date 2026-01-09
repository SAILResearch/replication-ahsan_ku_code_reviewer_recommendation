package com.sail.java.exam.work;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class OverrideDetector {

    private static OverrideDetector instance = null;

    private OverrideDetector() {

    }

    public static OverrideDetector getInstance() {
        if (OverrideDetector.instance == null) {
            instance = new OverrideDetector();
            return OverrideDetector.instance;
        } else {
            return OverrideDetector.instance;
        }
    }

    /* Determine whether two methods are matches */
    private boolean isMethodMatches(IMethodBinding firstMethod, IMethodBinding secondMethod) {

        /* Check first name */
        if (firstMethod.getName().equals(secondMethod.getName())) {

            /* Check number of parameters */
            if (firstMethod.getParameterTypes().length == secondMethod.getParameterTypes().length) {

                ITypeBinding mTypeFirst[] = firstMethod.getParameterTypes();
                ITypeBinding mTypeSecond[] = firstMethod.getParameterTypes();

                /* Check each parameter individually */
                for (int i = 0; i < mTypeFirst.length; i++) {
                    if (mTypeFirst[i].getName().equals(mTypeSecond[i].getName())) {

                    } else {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /* Determine overrides context */
    public IMethodBinding findOverrideInHierarchy(IMethodBinding methodBinding, ITypeBinding typebinding) {

        boolean findMatch = false;
        IMethodBinding overridenMethodBinding = null;

        if (typebinding.getSuperclass() != null) {
            // System.out.println("Class: "+className.getName()+"   Superclass"+
            // className.getSuperclass().getQualifiedName());
            ITypeBinding superTypeBinding = typebinding.getSuperclass();
            IMethodBinding methods[] = superTypeBinding.getDeclaredMethods();

            boolean returnValue = false;

            if ((overridenMethodBinding = matchMethodInType(methodBinding, superTypeBinding)) == null) {
            	overridenMethodBinding = findOverrideInHierarchy(methodBinding, superTypeBinding);
            } else {
            	//System.out.println("overridenMethodBinding: "+overridenMethodBinding.getDeclaringClass().getQualifiedName());
                //System.out.println(methodBinding.getKey() + " OverridesXXX " + overridenMethodBinding.getKey());
                //return overridenMethodBinding;
            }
        }
        if(overridenMethodBinding!=null) return overridenMethodBinding;
        /* search in super interfaces also */
        ITypeBinding[] interfaceBinds = typebinding.getInterfaces();
        for (ITypeBinding interfaceBind : interfaceBinds) {
            if ((overridenMethodBinding = matchMethodInType(methodBinding, interfaceBind)) == null) {
                if (interfaceBind.getSuperclass() != null) {
                	overridenMethodBinding =  findOverrideInHierarchy(methodBinding, interfaceBind.getSuperclass());
                }
            } else {
            	//System.out.println("overridenMethodBinding: "+overridenMethodBinding.getDeclaringClass().getQualifiedName());
                
                //System.out.println(methodBinding.getKey() + " OverridesYYY " + overridenMethodBinding.getKey());
            }
            
            if(overridenMethodBinding!=null) return overridenMethodBinding;
        }
        return null;
    }
    // Determine methods or interfaces overridden by the technique
    public void collectOverrideHierarchy(ITypeBinding typebinding, ArrayList<String> list) {
        if (typebinding == null) {
            return;
        }
        IMethodBinding overridenMethodBinding = null;

        // search in super classes
        if (typebinding.getSuperclass() != null) {

            ITypeBinding superTypeBinding = typebinding.getSuperclass();
            list.add(superTypeBinding.getQualifiedName());
            collectOverrideHierarchy(superTypeBinding, list);
        }

        /* search in super interfaces also */
        ITypeBinding[] interfaceBinds = typebinding.getInterfaces();
        for (ITypeBinding interfaceBind : interfaceBinds) {
            list.add(interfaceBind.getQualifiedName());
            if (interfaceBind.getSuperclass() != null) {
                collectOverrideHierarchy(interfaceBind.getSuperclass(), list);
            }
        }
    }

    /* Determine overrides context */
    // This version tries to determine whether we implement method of a particular class
   /* public IMethodBinding findOverrideInHierarchy(IMethodBinding methodBinding, ITypeBinding typebinding, boolean b) {

        boolean findMatch = false;
        IMethodBinding overridenMethodBinding = null;

        if (typebinding.getSuperclass() != null) {
            // System.out.println("Class: "+className.getName()+"   Superclass"+
            // className.getSuperclass().getQualifiedName());
            ITypeBinding superTypeBinding = typebinding.getSuperclass();
            IMethodBinding methods[] = superTypeBinding.getDeclaredMethods();

            boolean returnValue = false;

            if ((overridenMethodBinding = matchMethodInType(methodBinding, superTypeBinding)) == null) {
                findOverrideInHierarchy(methodBinding, superTypeBinding);
            } else {
                System.out.println(methodBinding.getKey() + " Overrides " + overridenMethodBinding.getKey());
                System.out.println("Intersting: "+typebinding.getQualifiedName());
                if (FrameworkUtility.isInteresting(typebinding)) {
                    return overridenMethodBinding;
                } else {
                    return null;
                }
            }
        }
        // search in super interfaces also 
        ITypeBinding[] interfaceBinds = typebinding.getInterfaces();
        for (ITypeBinding interfaceBind : interfaceBinds) {
            if ((overridenMethodBinding = matchMethodInType(methodBinding, interfaceBind)) == null) {
                if (interfaceBind.getSuperclass() != null) {
                    findOverrideInHierarchy(methodBinding, interfaceBind.getSuperclass());
                }
            } else {
                System.out.println(methodBinding.getKey() + " Overrides " + overridenMethodBinding.getKey());
            }
            if (FrameworkUtility.isInteresting(interfaceBind)) {
                return overridenMethodBinding;
            } else {
                return null;
            }
        }

        return null;
    }*/

    private IMethodBinding matchMethodInType(IMethodBinding methodBinding, ITypeBinding typeBinding) {

        /* iterate though all the methods within type bind */
        for (IMethodBinding tMethodBinding : typeBinding.getDeclaredMethods()) {
            if (isMethodMatches(tMethodBinding, methodBinding)) {
                return tMethodBinding;
            }
        }
        return null;
    }
}
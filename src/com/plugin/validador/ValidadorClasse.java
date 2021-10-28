package com.plugin.validador;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class ValidadorClasse extends AbstractHandler{
	
	private StringBuffer classWithProblems = new StringBuffer();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//System.out.println("Tratador invocado!");
		/*
		 * captura o item selecionado pelo usuário quando 
		 * ele seleciona um projeto com o botão direito
		 */
		IStructuredSelection itemSelected = (IStructuredSelection)
				HandlerUtil.getActiveMenuSelection(event);		
		Object firstElement = itemSelected.getFirstElement();		
		/*
		 * Verifica se o projeto do qual foi selecionado é um projeto Java
		 * ou está fechado, pois quando quando o projeto Eclipse encontra-se 
		 * fechado, não será possível descobrir o tipo do mesmo
		 */
		if(firstElement instanceof IJavaProject) {
			//objeto é convertido para uma instância do tipo IJavaProject
			IJavaProject myProject = (IJavaProject) firstElement;
			try {
				validateProject(myProject);
				if (classWithProblems.length() ==0) {
					MessageDialog.openInformation(
							null, 
							"Verification completed", 
							"There is no naming violation class in this project.");
				}else {
					MessageDialog.openError(
							null, 
							"Verification completed",
							"Naming violation class:\n" + classWithProblems);
				}
			} catch (JavaModelException e) {
				MessageDialog.openError(
						null, "Verificação falhou", 
						"An error occurred while checking the current project.");
			}
		}else {
			MessageDialog.openError(null, "Invalid project", 
					"It is not a java project or it's closed");
		}
		
		return null;
	}

	private void validateProject(IJavaProject myProject) throws JavaModelException {		
		IPackageFragment[] packages = myProject.getPackageFragments();
		for (IPackageFragment p : packages) {
			if(p.getKind() == IPackageFragmentRoot.K_SOURCE) {
				for(ICompilationUnit compilationUnit : p.getCompilationUnits()) {
					String classIdentification = compilationUnit.getElementName();
					if (!isValidIdentification(classIdentification)) {
						classWithProblems.append(classIdentification + "\n");
					}
				}
			}
		}
	}
	
	private boolean isValidIdentification (String id) {
		return (id.charAt(0) == id.toUpperCase().charAt(0));
	}

}

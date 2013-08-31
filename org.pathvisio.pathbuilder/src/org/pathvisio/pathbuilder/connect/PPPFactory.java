package org.pathvisio.pathbuilder.connect;

import org.pathvisio.core.data.GdbManager;
import org.pathvisio.plugins.BindPppPlugin;
import org.pathvisio.plugins.ConceptWikiSparqlPppPlugin;
import org.pathvisio.plugins.HmdbPppPlugin;
import org.pathvisio.plugins.KeggPppPlugin_getEnzymesByCompound;
import org.pathvisio.plugins.KeggPppPlugin_getEnzymesByGene;
import org.pathvisio.plugins.KeggPppPlugin_getGenesByEnzymes;
import org.pathvisio.plugins.LocalInteractionGpmlPppPlugin;
import org.pathvisio.plugins.LocalPathways;
import org.pathvisio.plugins.OpenPhactsApiPlugin;
import org.pathvisio.plugins.OpenPhactsPppPlugin;
import org.pathvisio.plugins.PathwayCommonsPppPlugin;
import org.pathvisio.plugins.StitchSparqlPppPlugin;
import org.pathvisio.plugins.Suggestion;
import org.pathvisio.plugins.WhatizitPppPlugin;
import org.pathvisio.plugins.WikiPathwaysPppPlugin;

public class PPPFactory {

	public enum Ppp {
		KEGG_GBYE("Kegg (Gene by enzyme)"),
		KEGG_EBYE("Kegg (Enzyme by gene)"),
		KEGG_EBYC("Kegg (Enzyme by compound)"),
		WIKI_LOCAL("WikiPathways (local)"),
		PC_ALL("PathwayCommons"),
		PC_BIOGRID("BIOGRID"),
		PC_CELL_MAP("CELL_MAP"),
		PC_HRPD("HPRD"),
		PC_HUMAN_CYC("HUMAN_CYC"),
		PC_INTACT("INTACT"),
		PC_MINT("MINT"),
		PC_NCI_NATURE("NCI_NATURE"),
		PC_REACTOME("REACTOME"),
		BIND("Bind"),
		CONCEPTWIKI("ConceptWiki"),
		OPEN_PHACTS("Find compound by Gene"),
		SPARQL("Find suggestions");
//		WHAT("Whatizit"),
//		WIKI("WikiPathways"),
//		LOCAL_GPML("Local Gpml Interaction"),
//		OPENPHACTS("Get Targets of Compound"),
//		HMDB("HMDB");
		
		private String string;
		
		private Ppp(String string){
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
		
	}
	
	public static Suggestion newPPP(GdbManager gdb,Ppp ppp) {
		switch (ppp){
		case KEGG_GBYE:
			return new KeggPppPlugin_getGenesByEnzymes(gdb);
		case KEGG_EBYE:
			return new KeggPppPlugin_getEnzymesByGene(gdb);
		case KEGG_EBYC:
			return new KeggPppPlugin_getEnzymesByCompound(gdb);
		case WIKI_LOCAL:
			return new LocalPathways();
		case PC_ALL:
			return new PathwayCommonsPppPlugin(gdb, PathwayCommonsPppPlugin.SOURCE_ALL);
		case PC_BIOGRID:
			return new PathwayCommonsPppPlugin(gdb, "BIOGRID");
		case PC_CELL_MAP:
			return new PathwayCommonsPppPlugin(gdb, "CELL_MAP");
		case PC_HRPD:
			return new PathwayCommonsPppPlugin(gdb, "HPRD");
		case PC_HUMAN_CYC:
			return new PathwayCommonsPppPlugin(gdb, "HUMANCYC");
		case PC_INTACT:
			return new PathwayCommonsPppPlugin(gdb, "INTACT");
		case PC_MINT:
			return new PathwayCommonsPppPlugin(gdb, "MINT");
		case PC_NCI_NATURE:
			return new PathwayCommonsPppPlugin(gdb, "NCI_NATURE");
		case PC_REACTOME:
			return new PathwayCommonsPppPlugin(gdb, "REACTOME");
		case BIND:
			return new BindPppPlugin(gdb);
		case CONCEPTWIKI:
			return new ConceptWikiSparqlPppPlugin(gdb);
		case OPEN_PHACTS:
			return  new OpenPhactsPppPlugin(gdb);
		case SPARQL:
			return new StitchSparqlPppPlugin(gdb);
//		case WHAT:
//			return new WhatizitPppPlugin(gdb);
//		case WIKI:
//			return new WikiPathwaysPppPlugin(gdb);
//		case LOCAL_GPML:
//			return new LocalInteractionGpmlPppPlugin(gdb);
//		case OPENPHACTS:
//			return new OpenPhactsApiPlugin(gdb);
//		case HMDB:
//			return new HmdbPppPlugin(gdb);
		}
		return null;
	}
}

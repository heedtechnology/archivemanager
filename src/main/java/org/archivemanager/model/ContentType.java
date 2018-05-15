package org.archivemanager.model;

/**
 * Created by jnewman on 5/2/18.
 */
public enum ContentType {

//  		if(in == null) return "";
//		if(in.equals("audio")) return "Audio";
//		if(in.equals("correspondence")) return "Correspondence";
//		if(in.equals("financial")) return "Financial Material";
//		if(in.equals("legal")) return "Legal Material";
//		if(in.equals("manuscript")) return "Manuscript";
//		if(in.equals("memorabilia")) return "Memorabilia";
//		if(in.equals("photographs")) return "Photographic Material";
//		if(in.equals("printed_material")) return "Printed Material";
//		if(in.equals("professional")) return "Professional Material";
//		if(in.equals("video")) return "Video";
//		if(in.equals("research")) return "Research";

  AUDIO ("audio","Audio"),
  VIDEO ("video","Video"),
  CORRESPONDENCE ("correspondence","Correspondence"),
  FINANCIAL ("financial","Financial Material"),
  LEGAL ("legal","Legal Material"),
  MANUSCRIPT ("manuscript","Manuscript"),
  MEMORABILIA ("memorabilia","Memorabilia"),
  PHOTOGRAPHS ("photgraphs","Photographic Material"),
  PRINTED_MATERIAL ("printed_material","Printed Material"),
  PROFESSIONAL ("professional","Professional"),
  RESEARCH ("research","Research");


  private final String key;
  private final String value;

  ContentType(String key, String value){
    this.key = key;
    this.value = value;
  }

  public String key() { return key; }
  public String value() { return value; }

}

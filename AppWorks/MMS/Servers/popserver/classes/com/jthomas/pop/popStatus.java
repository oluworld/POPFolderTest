package com.jthomas.pop;

public class popStatus {
   boolean _OK = false;
   String _Response;
   String[] _Responses = new String[0];

   public boolean OK() {
      return this._OK;
   }

   public String Response() {
      return this._Response;
   }

   public String[] Responses() {
      return this._Responses;
   }
}

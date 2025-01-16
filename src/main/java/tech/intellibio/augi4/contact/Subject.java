/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.contact;

/**
 *
 * @author leonard
 */
  enum Subject {
        Investor("Billionaire Tech Visionary Ready to Invest"),
        HR("HR fast Hire"),
        Vendor("Vendor/Partner ready to provide me AI as as Service"),
        Other("Other");

        private final String text;

        Subject(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

#
#   RPM package specification for Jpcap
#
%define jpcap_version 0.7
%define jpcap jpcap-%jpcap_version

Summary: A Java library for capturing and sending network packets
Name: jpcap
Version: %jpcap_version
Release: 1
License: LGPL
Group: Development/Java
Packager: Keita Fujii
Source: http://netresearch.ics.uci.edu/kfujii/jpcap/%jpcap.tar.gz
Vendor: Keita Fujii <kfujii@uci.edu>

BuildRequires: jdk >= 1.6, libpcap >= 0.9, libpcap-devel >= 0.9
Requires: jdk >= 1.6, libpcap >= 0.9
Autoreq: 0

%description
Jpcap is a Java library for capturing and
sending network packets from Java applications.

This Jpcap package requires Sun's JDK 1.6 or higher,
and libpcap 0.9 or higher.

%prep

%setup

%build
cd src/c
make clean
make

%pre 

%install
install -s src/c/libjpcap.so $RPM_BUILD_ROOT/usr/lib

# extension for JDK1.6
install -d $RPM_BUILD_ROOT/usr/java/packages/lib/ext
install lib/jpcap.jar $RPM_BUILD_ROOT/usr/java/packages/lib/ext

%post

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root)
%doc README COPYING ChangeLog
/usr/lib/libjpcap.so
/usr/java/packages/lib/ext/jpcap.jar

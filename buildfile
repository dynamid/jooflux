# ..................................................................................... #
#
# JooFlux
#
# Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
# Copyright (c) 2012 Julien Ponge, INSA-Lyon                                      
# Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon                                 
#                                                                                 
# This Source Code Form is subject to the terms of the Mozilla Public             
# License, v. 2.0. If a copy of the MPL was not distributed with this             
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#
# ..................................................................................... #
# General definitions
# ..................................................................................... #

JOOFLUX_VERSION = 'r1-SNAPSHOT'
ASM_LIB = 'lib/asm-all-4.0.jar'
TINYLOG_LIB = 'lib/tinylog.jar'
repositories.remote << 'http://mirrors.ibiblio.org/maven2/'

SCIMARK_URL = 'http://math.nist.gov/scimark2/scimark2lib.jar'
CLOJURE_URL = 'http://repo1.maven.org/maven2/org/clojure/clojure/1.4.0/clojure-1.4.0.jar'
DACAPO_URL = 'http://heanet.dl.sourceforge.net/project/dacapobench/9.12-bach/dacapo-9.12-bach.jar'

# ..................................................................................... #
# Modules
# ..................................................................................... #

desc 'JooFlux main module'
define 'jooflux' do
  
  project.group = 'fr.insalyon.telecom'
  project.version = JOOFLUX_VERSION
  package(:jar).merge(ASM_LIB).exclude('META-INF/MANIFEST.MF')
  package(:jar).merge(TINYLOG_LIB).exclude('META-INF/MANIFEST.MF')

  compile.options.lint = true
  compile.options.source = '1.7'
  compile.options.target = '1.7'
  compile.with Dir['lib/*.jar']
  
  manifest['Name'] = 'fr/insalyon/telecom/jooflux/'
  manifest['Specification-Title'] = 'JooFlux'
  manifest['Specification-Version'] = JOOFLUX_VERSION
  manifest['Specification-Vendor'] = 'INSA-Lyon'
  manifest['Implementation-Title'] = 'fr.insalyon.telecom.jooflux'
  manifest['Implementation-Version'] = JOOFLUX_VERSION
  manifest['Implementation-Vendor'] = 'INSA-Lyon'
  manifest['Copyright'] = 'Copyright (c) 2012 INSA-Lyon'
  manifest['License'] = 'Mozilla Public License Version 2.0'
  manifest['License-URL'] = 'http://www.mozilla.org/MPL/2.0/'

  manifest['Premain-Class'] = 'fr.insalyon.telecom.jooflux.InvokeInterceptorAgent'
  manifest['Can-Redefine-Classes'] = 'false'
  manifest['Can-Retransform-Classes'] = 'false'
  manifest['Can-Set-Native-Method-Prefix'] = 'false'
  
  desc 'JooFlux testing module'
  define 'testing' do
    package :jar
    manifest.delete('Premain-Class')    
    manifest.delete('Can-Redefine-Classes')
    manifest.delete('Can-Retransform-Classes')
    manifest.delete('Can-Set-Native-Method-Prefix')
  end

end

# ..................................................................................... #
# Tests
# ..................................................................................... #

ALL_TESTS = [
  :test_helloworld,
  :test_classloading,
  :test_fibonacci, 
  :test_reflective_fibonacci,
  :test_fibonacci_aspectj,
  :test_fibonacci_jruby,
  :test_reflective_fibonacci_jruby, 
  :test_fibonacci_groovy,
  :test_reflective_fibonacci_groovy,
  :test_fibonacci_clojure,
  :test_fibonacci_jython,
  :test_fibonacci_javascript,
  :test_forkjoin,
  :test_scimark2,
  :test_scimark2_large,
]

JOOFLUX_LOGGING = ENV['JOOFLUX_LOGGING'] ||= ''
LOGGING_FLAG = "-verbose:class #{JOOFLUX_LOGGING}"

task :test_all => ALL_TESTS do
  display "Running all tests"
end

SCIMARK2_LIB = 'lib/scimark2lib.jar'

task :test_scimark2 => :package do
  display "SciMark 2.0"
  sh "java -noverify -cp #{SCIMARK2_LIB} jnt.scimark2.commandline"
  display "SciMark 2.0 with JooFlux agent"
  sh "java -noverify #{LOGGING_FLAG} -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -cp #{ASM_LIB}:#{SCIMARK2_LIB} jnt.scimark2.commandline"
end

task :test_scimark2_large => :package do
  display "SciMark 2.0 Large"
  sh "java -noverify -cp #{SCIMARK2_LIB} jnt.scimark2.commandline -large"
  display "SciMark 2.0 Large with JooFlux agent"
  sh "java -noverify #{LOGGING_FLAG} -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -cp #{ASM_LIB}:#{SCIMARK2_LIB} jnt.scimark2.commandline -large"
end

DACAPO_LIB = 'lib/dacapo-9.12-bach.jar'

task :test_dacapo => :package do
  display "Dacapo"
  sh "java -noverify -jar #{DACAPO_LIB} -C luindex"
  sh "java -noverify -jar #{DACAPO_LIB} -C lusearch"
  sh "java -noverify -jar #{DACAPO_LIB} -C sunflow"
  sh "java -noverify -jar #{DACAPO_LIB} -C avrora"
  display "Dacapo with JooFlux agent"
  sh "java -noverify -Dtinylog.level=ERROR -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -cp #{ASM_LIB} -jar #{DACAPO_LIB} -C luindex"
  sh "java -noverify -Dtinylog.level=ERROR -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -cp #{ASM_LIB} -jar #{DACAPO_LIB} -C lusearch"
  sh "java -noverify -Dtinylog.level=ERROR -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -cp #{ASM_LIB} -jar #{DACAPO_LIB} -C sunflow"
  sh "java -noverify -Dtinylog.level=ERROR -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -cp #{ASM_LIB} -jar #{DACAPO_LIB} -C avrora"
end

task :test_forkjoin => :package do
  puts "\n** ForkJoin Tests Requirements **"
  puts "   Set $HOTSPOT_PATH to point to the OpenJDK hotspot module."
  puts "   Default is `../jdk7/macosx-port/hotspot`"
  jdk_hotspot_path = if ENV['HOTSPOT_PATH']
    ENV['HOTSPOT_PATH']
  else
    '../jdk7/macosx-port/hotspot'
  end
  puts "   Current is `#{jdk_hotspot_path}`"
  args = [
    jdk_hotspot_path,
    'List',
    '10',
    '-Xmx2048M -Xms512M'
  ]
  launch_test('ForkJoin', 'fr.insalyon.telecom.joofluxtest.forkjoin.WordCounter', args)
  launch_test_with_agent('ForkJoin', 'fr.insalyon.telecom.joofluxtest.forkjoin.WordCounter', args )
end

task :test_reflective_fibonacci => :package do
  launch_test('Reflective Fibonacci', 'fr.insalyon.telecom.joofluxtest.reflection.ReflectiveFibonacci')
  launch_test_with_agent('Reflective Fibonacci', 'fr.insalyon.telecom.joofluxtest.reflection.ReflectiveFibonacci')
end

task :test_classloading => :package do
  launch_test('Class.forName Loading', 'fr.insalyon.telecom.joofluxtest.classloading.TestClassLoading')
  launch_test_with_agent('Class.forName Loading', 'fr.insalyon.telecom.joofluxtest.classloading.TestClassLoading')
end

task :test_helloworld => :package do
  launch_test('HelloWorld', 'fr.insalyon.telecom.joofluxtest.helloworld.TestHelloWorld')
  launch_test_with_agent('HelloWorld', 'fr.insalyon.telecom.joofluxtest.helloworld.TestHelloWorld')
end

task :test_fibonacci => :package do
  launch_test('Fibonacci', 'fr.insalyon.telecom.joofluxtest.fibonacci.TestRecursive')  
  launch_test_with_agent('Fibonacci', 'fr.insalyon.telecom.joofluxtest.fibonacci.TestRecursive')
end

task :test_counterloop => :package do
  launch_test_with_agent('Counter loop', 'fr.insalyon.telecom.joofluxtest.counterloop.CounterLoop')
end

task :test_selfswitcher => :package do
  launch_test_with_agent('Self switcher', 'fr.insalyon.telecom.joofluxtest.self.switcher.Main')
end

task :test_guiswitcher => :package do
  launch_test_with_agent('GUI switcher', 'fr.insalyon.telecom.joofluxtest.gui.switcher.MyGUI')
end

ASPECTJ_HOME = ENV['ASPECTJ_HOME'] ||= ''

task :test_fibonacci_aspectj do
  display "Fibonacci intercepted by AspecJ (before)"
  sh "ajc -cp #{ASPECTJ_HOME}/lib/aspectjrt.jar:testing/aopplats testing/aopplats/FiboForAspectJ.java testing/aopplats/BTracer.aj"
  sh "aj -cp #{ASPECTJ_HOME}/lib/aspectjweaver.jar:testing/aopplats FiboForAspectJ"
  display "Fibonacci intercepted by AspecJ (after)"
  sh "ajc -cp #{ASPECTJ_HOME}/lib/aspectjrt.jar:testing/aopplats testing/aopplats/FiboForAspectJ.java testing/aopplats/ATracer.aj"
  sh "aj -cp #{ASPECTJ_HOME}/lib/aspectjweaver.jar:testing/aopplats FiboForAspectJ"
  display "Fibonacci intercepted by AspecJ (before+after)"
  sh "ajc -cp #{ASPECTJ_HOME}/lib/aspectjrt.jar:testing/aopplats testing/aopplats/FiboForAspectJ.java testing/aopplats/ABTracer.aj"
  sh "aj -cp #{ASPECTJ_HOME}/lib/aspectjweaver.jar:testing/aopplats FiboForAspectJ"
end

CLOJURE = 'lib/clojure-1.4.0.jar'

task :test_fibonacci_clojure => :package do
  display "Fibonacci on Clojure"
  sh "java -jar #{CLOJURE} testing/dynlangs/fibo.clj"
  display "Fibonacci on Clojure instrumented by JooFlux"
  sh "java -noverify #{LOGGING_FLAG} -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -Dtinylog.level=WARNING -Xbootclasspath/a:#{CLOJURE}:target/jooflux-#{JOOFLUX_VERSION}.jar clojure.main testing/dynlangs/fibo.clj"
end

task :test_fibonacci_jython do
  display "Fibonacci on Jython"
  sh "jython testing/dynlangs/fibo.py"
end

task :test_fibonacci_jruby do
  display "Fibonacci on JRuby"
  sh "jruby testing/dynlangs/fibo.rb"
end

task :test_reflective_fibonacci_jruby do
  display "Reflective Fibonacci on JRuby"
  sh "jruby testing/dynlangs/fibo-reflective.rb"
end

task :test_fibonacci_groovy do
  display "Fibonacci on Groovy"
  sh "groovy testing/dynlangs/fibo.groovy"
end

task :test_reflective_fibonacci_groovy do
  display "Reflective Fibonacci on Groovy"
  sh "groovy testing/dynlangs/fibo-reflective.groovy"
end

task :test_fibonacci_javascript do
  display "Fibonaci on Javascript"
  sh "node testing/dynlangs/fibo.js"
end

task :test_clj => :package do
  display "Clojure instrumented by JooFlux"
  sh "java -noverify #{LOGGING_FLAG} -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -Dtinylog.level=WARNING -Xbootclasspath/a:#{CLOJURE}:target/jooflux-#{JOOFLUX_VERSION}.jar clojure.main"
end

def launch_test(name, main_class, *extra_args)
  display name
  sh "java -noverify -cp testing/target/jooflux-testing-#{JOOFLUX_VERSION}.jar #{main_class} #{extra_args.join ' '}"
end

def launch_test_with_agent(name, main_class, *extra_args)
  display "#{name} with JooFlux agent"
  sh "java -noverify #{LOGGING_FLAG} -javaagent:target/jooflux-#{JOOFLUX_VERSION}.jar -cp testing/target/jooflux-testing-#{JOOFLUX_VERSION}.jar #{main_class} #{extra_args.join ' '}"
end

def display(message)
  puts "\n\e[1m\e[34m>>> #{message}\e[0m\n\n"
end

task :fetch_test_libs do
  sh "curl -o #{DACOPA_LIB} #{DACAPO_URL}"
  sh "curl -o #{SCIMARK2_LIB} #{SCIMARK_URL}"
  sh "curl -o #{CLOJURE} #{CLOJURE_URL}"
end

# ..................................................................................... #
